package com.marketmate.service;

import com.marketmate.entity.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LLMService {
    public String ask(List<ChatMessage> context, String model) {
        return "Mocked LLM response based on context.";
    }
}
