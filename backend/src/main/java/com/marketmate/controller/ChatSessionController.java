package com.marketmate.controller;

import com.marketmate.entity.ChatSession;
import com.marketmate.repository.ChatSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/chat/sessions")
public class ChatSessionController {

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @GetMapping
    public List<ChatSession> getSessions(@AuthenticationPrincipal UserDetails user) {
        return chatSessionRepository.findByUserId(user.getUsername());
    }

    @GetMapping("/{sessionId}")
    public Optional<ChatSession> getSession(@PathVariable Long sessionId) {
        return chatSessionRepository.findById(sessionId);
    }

    @PostMapping
    public ChatSession createSession(@AuthenticationPrincipal UserDetails user,
                                    @RequestParam String title) {
        ChatSession session = new ChatSession();
        session.setUserId(user.getUsername());
        session.setTitle(title);
        return chatSessionRepository.save(session);
    }
}
