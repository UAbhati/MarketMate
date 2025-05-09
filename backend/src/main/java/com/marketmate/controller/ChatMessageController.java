package com.marketmate.controller;

import com.marketmate.entity.ChatMessage;
import com.marketmate.entity.ChatSession;
import com.marketmate.repository.ChatMessageRepository;
import com.marketmate.repository.ChatSessionRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sessions/{sessionId}/messages")
public class ChatMessageController {
    @Autowired
    private ChatMessageRepository messages;
    @Autowired
    private ChatSessionRepository sessions;

    @GetMapping
    public List<ChatMessage> all(@PathVariable UUID sessionId) {
        sessions.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return messages.findBySession_IdOrderByCreatedAtAsc(sessionId);
    }

    @PostMapping
    public ChatMessage add(
            @PathVariable UUID sessionId,
            @RequestBody ChatMessage payload) {
        ChatSession s = sessions.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        payload.setSession(s);
        return messages.save(payload);
    }
}
