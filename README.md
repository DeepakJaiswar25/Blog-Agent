# Blog-Agent 📝

An AI-powered multi-agent content generation pipeline built with **Embabel**, **Spring AI**, **MCP**, **Tavily**, and **OpenAI**.

The application automatically researches a topic, generates a technical blog post, reviews and improves the content, creates a TLDR summary, generates SEO metadata, and publishes the final result as a Markdown file.

---

## Why This Project?

Traditional blog generators rely on a single prompt. This project demonstrates an agentic workflow where specialized AI agents collaborate to research, write, review, and publish technical content automatically.

---

## Features ✨

* Automated web research using Tavily AI
* AI-generated technical blog posts
* Draft review and content improvement
* Automatic TLDR generation
* YAML front matter generation
* Reading time calculation
* SEO tags and keyword generation
* Structured outputs using Java Records
* Markdown blog export

---

## Architecture 🏗️

```text
User Topic
    │
    ▼
Research Agent
(Tavily MCP)
    │
    ▼
Draft Writer Agent
(GPT-4o-mini)
    │
    ▼
Review Agent
(GPT-5-mini)
    │
    ▼
TLDR Generator
    │
    ▼
Front Matter Generator
    │
    ▼
Published Markdown Blog
```

Each agent performs a specific responsibility and passes structured output to the next stage of the workflow.

---

## Technology Stack 🛠️

* Java 21
* Spring Boot 3.5.14
* Spring AI 1.1.4
* Embabel Agent Framework 0.4.0-SNAPSHOT
* OpenAI GPT Models
* Tavily AI Search
* MCP (Model Context Protocol)
* Maven

---

## Key Concepts Demonstrated

* Multi-Agent AI Workflows
* Agentic AI Architecture
* MCP Integration
* Spring AI Tool Calling
* Structured Outputs using Java Records
* Prompt Engineering
* Automated Content Generation
* SEO Metadata Generation

---

## Project Structure 📂

```text
Blog-Agent/
├── src/main/java/com/deepak/embabel/Blog/Agent/
│   ├── BlogAgentApplication.java
│   ├── agent/
│   │   ├── BlogWriterAgent.java
│   │   ├── BlogAgentProperties.java
│   │   └── Personas.java
│   ├── entity/
│   │   ├── BlogPost.java
│   │   ├── ResearchedTopic.java
│   │   ├── DraftPost.java
│   │   ├── ReviewedPost.java
│   │   ├── FinalPost.java
│   │   ├── PublishedPost.java
│   │   └── FrontMatter.java
│   └── tool/
│       └── ReadingStatsTool.java
├── src/main/resources/
│   ├── application.yaml
│   └── server.json
├── blog-posts/
└── pom.xml
```

---

## Agent Workflow

### Research Agent

* Searches the web using Tavily MCP
* Produces a structured research summary

### Draft Writer Agent

* Generates the initial blog post
* Creates beginner-friendly explanations and examples

### Review Agent

* Improves readability and technical accuracy
* Provides review feedback

### TLDR Generator

* Creates a concise summary for readers

### Front Matter Generator

* Generates metadata such as:

  * Title
  * Description
  * Tags
  * Keywords
  * Reading Time
  * Slug

---

## Configuration ⚙️

Required Environment Variables:

```bash
OPENAI_API_KEY=your-openai-key
TAVILY_API_KEY=your-tavily-key
```

Application Configuration:

```yaml
blog-agent:
  output-dir: blog-posts
  number-of-keywords: 5
```

---

## Running the Application 🚀

```bash
mvn spring-boot:run
```

Generated blog posts will be saved in:

```text
blog-posts/
```

---

## Sample Output 📄

```yaml
---
title: "Getting Started with Spring Boot"
slug: getting-started-with-spring-boot
published: true
readTime: "5 min read"
tags:
  - Spring Boot
  - Java
---
```

```markdown
> **TLDR:** Spring Boot simplifies Java development by providing production-ready defaults and opinionated configurations.

# Getting Started with Spring Boot

Blog content...
```

---

## Future Enhancements

* Direct publishing to Medium or Dev.to
* Multi-language blog generation
* AI-generated cover images
* RAG-based knowledge sources
* GitHub Pages integration

---

## Author

**Deepak Jaiswar**

Built using Embabel, Spring AI, OpenAI, and Tavily MCP.
