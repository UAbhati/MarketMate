package com.marketmate.controller;

import com.marketmate.entity.ChatSession;
import com.marketmate.repository.ChatSessionRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class ChatSessionController {

    private final ChatSessionRepository repo;

    public ChatSessionController(ChatSessionRepository repo) {
        this.repo = repo;
    }

    @Operation(summary = "Create new chat session")
    @PostMapping
    public ChatSession create(@AuthenticationPrincipal UserDetails user, @RequestParam String title) {
        ChatSession session = new ChatSession();
        session.setUserId(user.getUsername());
        session.setTitle(title);
        return repo.save(session);
    }

    @Operation(summary = "Get current user's chat sessions")
    @GetMapping
    public List<ChatSession> getMySessions(@AuthenticationPrincipal UserDetails user) {
        return repo.findByUserId(user.getUsername());
    }

    @Operation(summary = "Get a single session by ID")
    @GetMapping("/{id}")
    public ChatSession getSession(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        ChatSession session = repo.findById(id).orElseThrow();
        if (!session.getUserId().equals(user.getUsername())) {
            throw new RuntimeException("Unauthorized");
        }
        return session;
    }
}
