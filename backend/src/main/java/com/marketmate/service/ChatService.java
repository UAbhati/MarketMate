package com.marketmate.service;

import com.marketmate.model.ChatMessage;
import com.marketmate.model.APIRequest;
import com.marketmate.model.APIResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ChatService {
    public APIResponse getLLMResponse(APIRequest request) {
        // Mocked LLM response logic
        ChatMessage lastUserMsg = request.getMessages().get(request.getMessages().size() - 1);

        ChatMessage aiMessage = new ChatMessage();
        aiMessage.setRole("AI");
        aiMessage.setContent("Mocked response to: " + lastUserMsg.getContent());
        aiMessage.setTimestamp(Instant.now().toEpochMilli());

        APIResponse response = new APIResponse();
        response.setId(UUID.randomUUID().toString());
        response.setPromptTokens(20);
        response.setCompletionTokens(10);
        response.setCreated(Instant.now().getEpochSecond());
        response.setMessage(aiMessage);
        response.setFinishReason("STOP");

        return response;
    }
}
