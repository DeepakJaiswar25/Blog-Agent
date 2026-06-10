package com.deepak.embabel.Blog.Agent.entity;

import java.util.List;

public record FrontMatter(String description, List<String> tags, List<String> keywords, String readTime) {
}
