package com.spring.ai.Service;


import com.spring.ai.dto.muscle;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AIService {

    private final ChatClient chatClient;

    private final EmbeddingModel embeddingModel;

    private final VectorStore vectorStore;

    public float[] getEmbedding(String text){
        return embeddingModel.embed(text);
    }

    public void ingestDataToVectorStore() {
        List<Document> movies = List.of(
                new Document("A thief who steals corporate secrets through the use of dream-sharing technology.",
                        Map.of("title", "Inception", "genre", "Sci-Fi", "year", 2010)),

                new Document("A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
                        Map.of("title", "Interstellar", "genre", "Sci-Fi", "year", 2014)),

                new Document("A poor yet passionate young man falls in love with a rich young woman, giving her a sense of freedom.",
                        Map.of("title", "The Notebook", "genre", "Romance", "year", 2004))
        );
        vectorStore.add(movies);
    }

    public List<Document> similaritySearch(String text) {
        return vectorStore.similaritySearch(SearchRequest.builder()
                .query(text)
                        .topK(1)
                        .similarityThreshold(0.1)
                .build());
    }

    public String getContent(String topic){

        String systemPrompt= """
                Youre a Science Teacher , You teach science based gym training.
                Give me the best workout for the muscle :{muscle}
                """;

        PromptTemplate promptTemplate =new PromptTemplate(systemPrompt);
        String renderText= promptTemplate.render(Map.of("muscle",topic));
        var Response= chatClient.prompt()
                .user(renderText)
                .call()
                .entity(muscle.class);
        return Response.text();
    }
}
