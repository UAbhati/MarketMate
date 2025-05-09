package com.marketmate.entity;

import java.time.Instant;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable=false)
    @JsonBackReference
    private ChatSession session;

    protected ChatMessage() {}

    public ChatMessage(ChatSession session, String role, String content) {
        this.session = session;
        this.role = role;
        this.content = content;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ChatSession getSession() {
        return session;
    }

    public void setSession(ChatSession session) {
        this.session = session;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
