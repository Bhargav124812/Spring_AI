package com.spring.ai.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigAI {

    @Bean
    public ChatClient chartClient(ChatClient.Builder builder){
        return builder
                .build();
    }

}
