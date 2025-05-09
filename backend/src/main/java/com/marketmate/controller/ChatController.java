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
    private FinancialRelatedQuestions financialRelatedQuestions;

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
            @RequestParam String tier
    ) {
        ChatSession session = sessionRepo.findById(sessionId)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!session.getUserId().equals(getCurrentUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (!financialRelatedQuestions.isFinancialQuery(message.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Please ask only financial-market-related questions.");
        }

        // 1. Build context and call LLM
        APIResponse aiResp = chatService.buildContextAndCallLLM(
                sessionId, getCurrentUserId(), message, model);
        // 2) enforce RPM
        rateLimitService.checkAllLimits(
            getCurrentUserId(),
            model,
            aiResp.getPromptTokens(),
            aiResp.getCompletionTokens()
        );
        // 3. Save messages + usage
        chatService.saveMessagesAndTrack(
                session,
                message,
                aiResp.getMessage().getContent(),
                getCurrentUserId(),
                model,
                tier,
                aiResp.getPromptTokens(),
                aiResp.getCompletionTokens());

        return aiResp.getMessage();
    }

    private String getCurrentUserId() {
        // extract from SecurityContext or JWT…
        return SecurityContextHolder.getContext()
        .getAuthentication().getName();
    }
}
