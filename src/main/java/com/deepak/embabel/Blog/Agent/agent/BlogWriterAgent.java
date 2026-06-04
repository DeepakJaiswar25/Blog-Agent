package com.deepak.embabel.Blog.Agent.agent;

import com.deepak.embabel.Blog.Agent.entity.BlogDraft;
import com.deepak.embabel.Blog.Agent.entity.ReviewedPost;
import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.Ai;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.common.ai.model.LlmOptions;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

@Agent(
        name = "Blog Writer Agent",
        description = "An agent that writes and reviews a blog post about a given topic"
)
public class BlogWriterAgent {

  private final BlogAgentProperties blogAgentProperties;
  private static final org.slf4j.Logger log = LoggerFactory.getLogger(BlogWriterAgent.class);
  public BlogWriterAgent(BlogAgentProperties blogAgentProperties) {
    this.blogAgentProperties = blogAgentProperties;
  }


    @Action(description = "Write a first draft of the blog post")
      public BlogDraft writeDraft(UserInput userInput, Ai ai){
          return ai.withLlm(LlmOptions.withDefaultLlm())
                  .withId("blog-post-draft-writer")
                  .withPromptContributor(Personas.BLOG_WRITER)
                  .creating(BlogDraft.class)
                  .fromPrompt("""
                          Write a blog post about: %s
                          Keep it practical and beginner friendly.
                          Use short sentences and plain Language.
                          Include code examples but keep them short and simple.
                          Write the content in Markdown.
                          """.formatted(userInput.getContent()));
      }

      @AchievesGoal(description = "Produce a final version of the blog post that is ready for publication")
      @Action(description = "Review and improve the draft")
      public ReviewedPost reviewDraft(BlogDraft blogDraft, Ai ai){
        ReviewedPost reviewed= ai.withLlm(LlmOptions.withLlmForRole("reviewer"))
                .withId("blog-post-reviewer")
                .withPromptContributor(Personas.BLOG_REWIEWER)
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

  private void writeToFile(ReviewedPost post) {
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
