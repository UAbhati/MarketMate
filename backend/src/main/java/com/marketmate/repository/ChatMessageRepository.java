package com.marketmate.repository;

import com.marketmate.entity.ChatMessage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySession_Id(Long sessionId);
}
