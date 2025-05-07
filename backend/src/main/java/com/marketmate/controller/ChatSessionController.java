package com.marketmate.controller;

import com.marketmate.entity.ChatSession;
import com.marketmate.repository.ChatSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/chat/sessions")
public class ChatSessionController {

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @GetMapping
    public List<ChatSession> getSessions(@RequestParam String userId) {
        return chatSessionRepository.findByUserId(userId);
    }

    @GetMapping("/{sessionId}")
    public Optional<ChatSession> getSession(@PathVariable Long sessionId) {
        return chatSessionRepository.findById(sessionId);
    }

    @PostMapping
    public ChatSession createSession(@RequestParam String userId, @RequestParam String title) {
        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setTitle(title);
        return chatSessionRepository.save(session);
    }
}
