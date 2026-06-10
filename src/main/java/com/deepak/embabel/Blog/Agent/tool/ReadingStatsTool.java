package com.deepak.embabel.Blog.Agent.tool;

import com.embabel.agent.api.annotation.LlmTool;
import org.springframework.stereotype.Component;

@Component
public class ReadingStatsTool {

     private static final int WORDS_PER_MINUTE=200;


     @LlmTool(description = "Calculate word count and estimated reading time (in minutes) for a piece of text. Reading speed is assumed to be 200 words per minute.")
    public String CalculateReadingStats(@LlmTool.Param(description = "The full text of blog post to analyze") String text){

         if(text== null || text.isEmpty()){
             return "0 words, 0 min read";
         }
         int words= text.trim().split("\\s+").length;
         int minutes= Math.max(1, (int) Math.ceil((double) words / WORDS_PER_MINUTE));
         return String.format("%d words, %d min read", words, minutes);
     }
}
