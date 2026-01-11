package com.spring.ai.Service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class RAG_ServiceTest {

    @Autowired
    private  RAG_Service ragService;

    @Test
    void ingestVectorStore() {
        ragService.ingestVectorStore();
    }

    @Test
    void askAI() {
        String res= ragService.askAI("How Can i do abdomin workout");
        System.out.println(res);
    }
}