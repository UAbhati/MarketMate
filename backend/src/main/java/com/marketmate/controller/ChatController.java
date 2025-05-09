package com.marketmate.controller;

import com.marketmate.entity.ChatMessage;
import com.marketmate.entity.ChatSession;
import com.marketmate.repository.ChatSessionRepository;
import com.marketmate.service.ChatService;
import com.marketmate.service.RateLimitService;

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
            @RequestParam String tier) {
        ChatSession session = sessionRepo.findById(sessionId)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!session.getUserId().equals(getCurrentUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // 2) enforce RPM
        rateLimitService.checkRateLimit(getCurrentUserId());
        // 3) delegate to service, which already uses the stored system message +
        // history
        String aiReplyContent = chatService.handleMessage(sessionId, 
                getCurrentUserId(), message,
                model, tier);
        // The chatService returns just the AI reply text for the prompt.

        // Return the AI reply as a ChatMessage object (or create a DTO)
        ChatMessage aiMessage = new ChatMessage(null, "assistant", aiReplyContent);
        // Note: session is null here to avoid including session data in JSON response
        return aiMessage;
    }

    private String getCurrentUserId() {
        // extract from SecurityContext or JWT…
        return SecurityContextHolder.getContext()
        .getAuthentication().getName();
    }
}
