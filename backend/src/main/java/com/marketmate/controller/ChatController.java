package com.marketmate.controller;

import com.marketmate.model.APIRequest;
import com.marketmate.model.APIResponse;
import com.marketmate.model.ChatMessage;
import com.marketmate.service.ChatService;
import com.marketmate.service.FinancialDataService;
import com.marketmate.service.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private RateLimitService rateLimitService;

    @Autowired
    private FinancialDataService financialDataService;

    @PostMapping
    public APIResponse chat(@AuthenticationPrincipal UserDetails user,
                        @RequestBody APIRequest request) {
        String userId = user.getUsername();
        if (rateLimitService.isRateLimitExceeded(userId)) {
            ChatMessage error = new ChatMessage();
            error.setRole("system");
            error.setContent("Rate limit exceeded. Please upgrade your plan or try again later.");
            error.setTimestamp(Instant.now().toEpochMilli());
            APIResponse response = new APIResponse();
            response.setMessage(error);
            return response;
        }

        if (request.getMessages() == null || request.getMessages().isEmpty()) {
            // Inject system message for new session
            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage systemMessage = new ChatMessage();
            systemMessage.setRole("system");
            systemMessage.setContent("You are a financial assistant. Only respond to financial market questions.");
            systemMessage.setTimestamp(Instant.now().toEpochMilli());
            messages.add(systemMessage);
            request.setMessages(messages);
        }

        return chatService.getLLMResponse(request);
    }
}
