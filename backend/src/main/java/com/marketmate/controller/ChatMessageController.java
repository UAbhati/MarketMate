package com.marketmate.controller;

import com.marketmate.entity.ChatMessage;
import com.marketmate.entity.ChatSession;
import com.marketmate.repository.ChatMessageRepository;
import com.marketmate.repository.ChatSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/chat/messages")
public class ChatMessageController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @PostMapping("/{sessionId}")
    public ChatMessage addMessage(@PathVariable Long sessionId, @RequestBody ChatMessage message) {
        Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionId);
        if (sessionOpt.isPresent()) {
            message.setSession(sessionOpt.get());
            return chatMessageRepository.save(message);
        }
        throw new RuntimeException("Session not found");
    }

    @GetMapping("/{sessionId}")
    public List<ChatMessage> getMessages(@PathVariable Long sessionId) {
        return chatMessageRepository.findBySession_Id(sessionId);
    }
}
