package com.marketmate.controller;

import com.marketmate.entity.ChatMessage;
import com.marketmate.entity.ChatSession;
import com.marketmate.model.APIResponse;
import com.marketmate.repository.ChatSessionRepository;
import com.marketmate.service.ChatService;
import com.marketmate.service.RateLimitService;
import com.marketmate.util.FinancialRelatedQuestions;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;
    @Autowired private RateLimitService rateLimitService;
    @Autowired private ChatSessionRepository sessionRepo;
    @Autowired private FinancialRelatedQuestions financialRelatedQuestions;

     @Operation(
        summary = "Send a message to the current session",
        parameters = {
        @Parameter(
            name="sessionId", in=ParameterIn.QUERY,
            description="UUID of the chat session",
            schema=@Schema(type="string", format="uuid")
        ),
        @Parameter(
            name="model", in=ParameterIn.QUERY,
            description="LLM model",
            schema=@Schema(allowableValues={"gpt-3.5","gpt-4","gpt-4o"})
        ),
        @Parameter(
            name="tier", in=ParameterIn.QUERY,
            description="Subscription tier",
            schema=@Schema(allowableValues={"FREE","TIER_1","TIER_2","TIER_3"})
        ),
        @Parameter(
            name="useRealLLM", in=ParameterIn.QUERY,
            description="Weather to use llm model or mock",
            schema=@Schema(type="boolean", defaultValue = "true")
        )
        }
    )
    @ApiResponse(responseCode = "200", description = "Assistant reply returned")
    @PostMapping
    public ChatMessage ask(
            @Parameter(hidden = true)
            @AuthenticationPrincipal 
            UserDetails user,
            @RequestParam UUID sessionId,
            @RequestParam String message,
            @RequestParam String model,
            @RequestParam String tier,
            @RequestParam(required = false, defaultValue = "true") boolean useRealLLM
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ChatSession session = sessionRepo.findById(sessionId)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!session.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (message == null || message.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message cannot be empty");
        }

        // 1. Build context and call LLM
        APIResponse aiResp = chatService.buildContextAndCallLLM(
                sessionId, userId, message, model, useRealLLM);
        // 2. Only now enforce domain relevance
        if (!financialRelatedQuestions.isFinancialQuery(message.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Please ask only financial-market-related questions.");
        }
        // 3. enforce RPM
        rateLimitService.checkAllLimits(
            userId,
            model,
            aiResp.getPromptTokens(),
            aiResp.getCompletionTokens()
        );
        // 4. Save messages + usage
        chatService.saveMessagesAndTrack(
                session,
                message,
                aiResp.getMessage().getContent(),
                userId,
                model,
                tier,
                aiResp.getPromptTokens(),
                aiResp.getCompletionTokens());

        return aiResp.getMessage();
    }
}
