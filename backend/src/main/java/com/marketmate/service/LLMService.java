package com.marketmate.service;

import com.marketmate.entity.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LLMService {
    public String ask(List<ChatMessage> context, String model) {
        // call your real LLM provider or just mock:
        return "Mocked LLM reply to `" + context.get(context.size() - 1).getContent() + "`";
    }
}
