package com.spring.ai.Service;


import com.spring.ai.dto.muscle;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AIService {

    private final ChatClient chatClient;

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
