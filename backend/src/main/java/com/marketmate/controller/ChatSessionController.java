package com.marketmate.controller;

import com.marketmate.entity.ChatMessage;
import com.marketmate.entity.ChatSession;
import com.marketmate.repository.ChatMessageRepository;
import com.marketmate.repository.ChatSessionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
@SecurityRequirement(name = "bearerAuth") // if you’ve defined a JWT bearer scheme in your OpenAPI config
public class ChatSessionController {

    private final ChatSessionRepository sessionRepo;
    @Autowired private ChatMessageRepository messageRepo;

    public ChatSessionController(ChatSessionRepository sessionRepo, ChatMessageRepository messageRepo) {
        this.sessionRepo = sessionRepo;
        this.messageRepo = messageRepo;
    }

    private String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return auth.getName();
    }

    public static class CreateSessionRequest {
        public String title;
    }

    @Operation(summary = "Create a new chat session (authenticated)")
    @Transactional
    @PostMapping
    public ChatSession createSession(@RequestBody CreateSessionRequest request) {
        if (request.title == null || request.title.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title cannot be empty");
        }
        String userId = currentUserId();

        ChatSession session = new ChatSession(userId, request.title.trim());
        sessionRepo.save(session);

        // ** NEW: seed the system prompt **
        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setSession(session);
        systemMessage.setRole("system");
        systemMessage.setContent(
                "You are MarketMate, a strict financial assistant. You must only answer questions directly " +
                "related to the financial markets, companies, stocks, economy, or investments. " +
                "If a question is not financial in nature, clearly respond: 'Sorry, I can only help " +
                "with financial-market-related questions.' Do not attempt to answer unrelated queries."
        );
        messageRepo.save(systemMessage);

        return session;
    }

    @Operation(summary = "List all of the current user's sessions")
    @GetMapping
    public List<ChatSession> listMySessions() {
        return sessionRepo.findByUserId(currentUserId());
    }

    @Operation(summary = "Fetch a single session along with its messages by UUID (authenticated)")
    @GetMapping("/{id}")
    public ChatSession getSessionWithMessages(@PathVariable UUID id) {
        ChatSession session = sessionRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!session.getUserId().equals(currentUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your session");
        }
        // Ensure messages are loaded (e.g., by JPA fetch or manually loading)
        session.getMessages().size(); // force lazy load if necessary
        return session; // Jackson will serialize messages list but not the back-reference to session
    }
}
