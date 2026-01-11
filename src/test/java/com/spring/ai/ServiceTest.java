package com.spring.ai;

import com.spring.ai.Service.AIService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ServiceTest {


    @Autowired
    private AIService aiService;
    @Test
    void getJock() {
        var joke = aiService.getContent("Bicep");
        System.out.println(joke);
    }

    @Test
    public void textEmbed(){
        var embed=aiService.getEmbedding("This the a Text to Embed");
        System.out.println(embed.length);
        for(float e: embed) {
            System.out.println(e+" ");
        }
    }

    @Test
    public void testStoreData() {
        aiService.ingestDataToVectorStore();
    }

    @Test
    public void testSimilaritySearch() {
        var res = aiService.similaritySearch("The Movie released in 2010 and its genre is Sci-Fi");
        for(var doc: res) {
            System.out.println(doc);
        }

    }
}