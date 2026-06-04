package com.deepak.embabel.Blog.Agent;

import com.deepak.embabel.Blog.Agent.agent.BlogAgentProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(BlogAgentProperties.class)
public class BlogAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogAgentApplication.class, args);
	}

}
