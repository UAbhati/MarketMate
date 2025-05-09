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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
@SecurityRequirement(name = "bearerAuth") // if you’ve defined a JWT bearer scheme in your OpenAPI config
public class ChatSessionController {

    private final ChatSessionRepository repo;
    @Autowired private ChatMessageRepository messageRepo;

    public ChatSessionController(ChatSessionRepository repo) {
        this.repo = repo;
    }

    private String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return auth.getName();
    }

    @Operation(summary = "Create a new chat session (authenticated)")
    @PostMapping
    public ChatSession createSession(@RequestParam String title) {
        String userId = currentUserId();

        ChatSession session = new ChatSession(userId, title);
        session.setId(UUID.randomUUID());
        session.setUserId(userId);
        session.setTitle(title);
        repo.save(session);

        // ** NEW: seed the system prompt **
        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setSession(session);
        systemMessage.setRole("system");
        systemMessage.setContent(
          "You are MarketMate, a financial assistant. " +
          "Only respond to financial-market questions. Provide concise, data-driven answers."
        );
        messageRepo.save(systemMessage);

        return session;
    }

    @Operation(summary = "List all of the current user's sessions")
    @GetMapping
    public List<ChatSession> listMySessions() {
        String userId = currentUserId();
        return repo.findByUserId(userId);
    }

    @Operation(summary = "Fetch a single session along with its messages by UUID (authenticated)")
    @GetMapping("/{id}")
    public ChatSession getSessionWithMessages(@PathVariable UUID id) {
        String userId = currentUserId();
        ChatSession session = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!session.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your session");
        }
        // Ensure messages are loaded (e.g., by JPA fetch or manually loading)
        session.getMessages().size(); // force lazy load if necessary
        return session; // Jackson will serialize messages list but not the back-reference to session
    }
}
