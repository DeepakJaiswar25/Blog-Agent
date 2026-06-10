package com.deepak.embabel.Blog.Agent.entity;

public record PublishedPost(String title, String content, String feedback) implements BlogPost {
}
