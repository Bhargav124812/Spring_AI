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
}