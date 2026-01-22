package com.spring.ai.Service;


import com.spring.ai.advisor.TokenUsageAdvisor;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RAG_Service {

    private final ChatClient chatClient;

    private final EmbeddingModel embeddingModel;

    private final VectorStore vectorStore;

    private final ChatMemory chatMemory;

    @Value("classpath:gym_exercises_guide.pdf")
    Resource gym_pdf;

    public void ingestVectorStore(){
        PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(gym_pdf);
        List<Document> documents = pagePdfDocumentReader.read();

        TokenTextSplitter tokenTextSplitter =new TokenTextSplitter(200,50,5,10000,true);
        List<Document> chunks= tokenTextSplitter.apply(documents);

        vectorStore.add(chunks);
    }

    public String askAIWithAdvisors(String prompt, String userId) {
        return chatClient.prompt()
                .system("""
                        You are an AI assistant called Cody.
                        Greet users with your Name (Cody) and the user name if you know their name.
                        Answer in a friendly, conversational tone.
                        """)
                .user(prompt)
                .advisors(
//                        new SafeGuardAdvisor(List.of("Politics", "Gaming")),

                        new TokenUsageAdvisor(),

                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .conversationId(userId)
                                .build(),

                        VectorStoreChatMemoryAdvisor.builder(vectorStore)
                                .conversationId(userId)
                                .defaultTopK(4)
                                .build(),

                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .filterExpression("file_name == 'gym_exercises_guide.pdf'")
                                        .topK(4)
                                        .build())
                                .build()
                )
                .call()
                .content();
    }

    public String askAI(String prompt){
        String template = """
                You are an AI assistant called Cody
                
                Rules:
                - Use ONLY the information provided in the context
                - You MAY rephrase, summarize, and explain in natural language
                - Do NOT introduce new concepts or facts
                - If multiple context sections are relevant, combine them into a single explanation.
                - If the answer is not present, say "I don't know"
                
                Context:
                {context}
                
                Answer in a friendly, conversational tone.
                """;

        var docs= vectorStore.similaritySearch(SearchRequest.builder()
                .query(prompt)
                .similarityThreshold(0.3)
                .filterExpression("file_name=='gym_exercises_guide.pdf'")
                .topK(3)
                .build());

        var context = docs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        PromptTemplate promptTemplate = new PromptTemplate(template);
        String stuffedPrompt = promptTemplate.render(Map.of("context", context));

        return chatClient.prompt()
                .system(stuffedPrompt)
                .user(prompt)
                .advisors(new SimpleLoggerAdvisor())
                .call()
                .content();
    }

}
