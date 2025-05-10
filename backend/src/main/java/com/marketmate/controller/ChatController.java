package com.marketmate.controller;

import com.marketmate.entity.ChatMessage;
import com.marketmate.entity.ChatSession;
import com.marketmate.model.APIResponse;
import com.marketmate.repository.ChatSessionRepository;
import com.marketmate.service.ChatService;
import com.marketmate.service.RateLimitService;
import com.marketmate.util.ContextBuilder;
import com.marketmate.util.FinancialRelatedQuestions;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;
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

        // simple greeting handler
        String trimmed = message.trim().toLowerCase();
        if (trimmed.equals("hi") || trimmed.equals("hello")) {
            ChatMessage reply = new ChatMessage();
            reply.setRole("assistant");
            reply.setContent(
                    "👋 Hello! I can help you with questions about the financial market.\n\n💡 Example: 'What is the PE ratio of Infosys?' or 'Give Q1 results of TCS'.");
            return new APIResponse(reply, 0, 0).getMessage();
        }

        // 1. Estimate prompt tokens from history
        List<ChatMessage> history = chatService.getHistory(sessionId);
        List<ChatMessage> context = ContextBuilder.buildWindow(session, history);
        context.add(new ChatMessage(session, "user", message));

        int estimatedPromptTokens = context.stream()
                .mapToInt(m -> m.getContent().split("\\s+").length)
                .sum();

        // 2. Run rate limit check BEFORE LLM call
        rateLimitService.checkAllLimits(
            userId,
            model,
            estimatedPromptTokens,
            0 // unknown yet, but completionTokens will be added after
        );

        // 3. Enforce financial domain
        if (!financialRelatedQuestions.isFinancialQuery(message.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Non-financial query");
        }

        // 4. Actually call LLM
        APIResponse aiResp = chatService.buildContextAndCallLLM(
        sessionId, userId, message, model, useRealLLM);
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
