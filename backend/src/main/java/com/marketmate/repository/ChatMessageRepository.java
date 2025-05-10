package com.marketmate.repository;

import com.marketmate.entity.ChatMessage;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    long countBySession_IdAndRole(UUID sessionId, String role);

    List<ChatMessage> findTop5BySession_IdAndRoleOrderByCreatedAtAsc(UUID sessionId, String role);

    List<ChatMessage> findBySession_IdOrderByCreatedAtAsc(UUID sessionId);
}
