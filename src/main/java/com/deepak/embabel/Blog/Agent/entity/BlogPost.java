package com.deepak.embabel.Blog.Agent.entity;

public sealed interface BlogPost permits DraftPost, FinalPost, PublishedPost, ReviewedPost{
    String title();
    String content();
}
