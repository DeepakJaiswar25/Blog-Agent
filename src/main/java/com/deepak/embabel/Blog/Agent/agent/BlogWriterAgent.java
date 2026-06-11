package com.deepak.embabel.Blog.Agent.agent;

import com.deepak.embabel.Blog.Agent.entity.*;
import com.deepak.embabel.Blog.Agent.tool.ReadingStatsTool;
import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.Ai;
import com.embabel.agent.core.CoreToolGroups;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.common.ai.model.LlmOptions;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Agent(
        name = "Blog Writer Agent",
        description = "An agent that writes and reviews a blog post about a given topic"
)
public class BlogWriterAgent {

  private final BlogAgentProperties blogAgentProperties;
  private static final org.slf4j.Logger log = LoggerFactory.getLogger(BlogWriterAgent.class);
  private  final ReadingStatsTool readingStatsTool;
  public BlogWriterAgent(BlogAgentProperties blogAgentProperties, ReadingStatsTool readingStatsTool) {
    this.blogAgentProperties = blogAgentProperties;
      this.readingStatsTool = readingStatsTool;
  }


  @Action(description = "Research the topic using web search")
  public ResearchedTopic getResearchedTopic(UserInput userInput,Ai ai) {
    return ai.withLlm(LlmOptions.withDefaultLlm())
            .withToolGroup("tavily")
            .withId("blog-post-researcher")
            .creating(ResearchedTopic.class)
            .fromPrompt("""
                        Research the following topic using web search tools.
                        Find current, relevant, and accurate information.
                        Limit yourself to no more than 3 web searches to avoid rate limiting.

                        Topic: %s

                        Provide the original topic and a concise summary
                        of your findings that would be useful for writing a blog post.
                        """.formatted(userInput.getContent()));
  }


    @Action(description = "Write a first draft of the blog post")
      public DraftPost writeDraft(ResearchedTopic researchedTopic, Ai ai){
          return ai.withLlm(LlmOptions.withDefaultLlm().withMaxTokens(16384))
                  .withId("blog-post-draft-writer")
                  .withPromptContributors(List.of(Personas.BLOG_WRITER,Personas.JSON_OUTPUT))
                  .creating(DraftPost.class)
                  .fromPrompt("""
                        Write a blog post about: %s

                        Use the following research to inform your writing:
                        %s

                        Keep it practical and beginner friendly.
                        Use short sentences and plain language.
                        Include code examples but keep them short and simple.
                        Write the content in Markdown.
                        """.formatted(researchedTopic.topic(), researchedTopic.research()));
      }

      @Action(description = "Review and improve the draft")
      public ReviewedPost reviewDraft(DraftPost blogDraft, Ai ai){
        ReviewedPost reviewed= ai.withLlm(LlmOptions.withLlmForRole("reviewer"))
                .withId("blog-post-reviewer")
                .withPromptContributors(List.of(Personas.BLOG_REWIEWER,Personas.JSON_OUTPUT))
                .creating(ReviewedPost.class)
                .fromPrompt("""
                       
                        Title: %s
                        Content: %s
                        
                        Fix any technical errors. Tighten the writing.
                        Provide the revised title, revised content, and a brief summary of the changes you made as feedback.
                        """.formatted(blogDraft.title(), blogDraft.content()));

         writeToFile(reviewed);
        return reviewed;
      }

      @Action(description = "An agent that creates a TLDR summary for a blog post and add to the top of blog post")
      public FinalPost addTLDR(ReviewedPost reviewedPost, Ai ai){
        String tldr = ai
                .withDefaultLlm()
                .withId("blog-post-tldr")
                .creating(String.class)
                .fromPrompt("""
                        Write a one or two sentence TLDR summary for this blog post.
                        Return only the summary text, nothing else.

                        Title: %s
                        Content:
                        %s
                        """.formatted(reviewedPost.title(), reviewedPost.content()));

        String contentWithTldr = "> **TLDR:** " + tldr + "\n\n" + reviewedPost.content();

        return new FinalPost(reviewedPost.title(), contentWithTldr, reviewedPost.feedback());
      }

      @AchievesGoal(description = "A reviewed and polished blog post with front matter")
      @Action(description = "Add front matter to the top of the blog post")
      public PublishedPost addFrontMatter(FinalPost post, Ai ai){
        FrontMatter frontMatter = ai
                .withDefaultLlm()
                .withToolObject(readingStatsTool)
                .withId("blog-post-front-matter")
                .withPromptContributors(List.of(Personas.JSON_OUTPUT))
                .creating(FrontMatter.class)
                .fromPrompt("""
                        Generate front matter metadata for this blog post.
                        Provide a concise description (1-2 sentences), relevant tags, and up to %d keywords.

                        Use the calculateReadingStats tool on the post content below to compute
                        the read time. Put the tool's exact return string into the readTime field.

                        Title: %s
                        Content:
                        %s
                        """.formatted(blogAgentProperties.numberOfKeywords(), post.title(), post.content()));

        String slug= post.title()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");

        String tags = frontMatter.tags().stream()
                .map(tag -> "  - " + tag)
                .collect(Collectors.joining("\n"));

        String keywords = frontMatter.keywords().stream()
                .map(keyword -> "  - " + keyword)
                .collect(Collectors.joining("\n"));
        String frontMatterBlock = """
                ---
                title: "%s"
                slug: %s
                date: "%sT08:00:00.000Z"
                published: true
                description: "%s"
                author: "Deepak Jaiswar"
                readTime: "%s"
                tags:
                %s
                keywords:
                %s
                ---
                """.formatted(
                post.title(),
                slug,
                LocalDate.now(),
                frontMatter.description(),
                frontMatter.readTime(),
                tags,
                keywords
        );
        String contentWithFrontMatter = frontMatterBlock + "\n" + post.content();
        PublishedPost publishedPost = new PublishedPost(post.title(), contentWithFrontMatter, post.feedback());
        writeToFile(publishedPost);
        return publishedPost;
      }

  private void writeToFile(BlogPost post) {
    String filename = post.title()
            .toLowerCase()
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-|-$", "")
            + ".md";

    Path outputDir = Path.of(blogAgentProperties.outputDir());
    Path filePath = outputDir.resolve(filename);

    try {
      Files.createDirectories(outputDir);
      Files.writeString(filePath, post.content());
      log.info("Blog post written to {}", filePath.toAbsolutePath());
    } catch (IOException e) {
      log.error("Failed to write blog post to {}: {}", filePath, e.getMessage());
    }
  }
}
