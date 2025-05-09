package com.marketmate.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "usage_record")
public class UsageRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String model;
    private String tier;

    private long requestCount;
    private long tokenCount;

    private Instant lastUsed;

    // No‐arg constructor for JPA
    public UsageRecord() {
    }

    // Convenience constructor
    public UsageRecord(String userId, String model, String tier) {
        this.userId = userId;
        this.model = model;
        this.tier = tier;
        this.requestCount = 0;
        this.tokenCount = 0;
        this.lastUsed = Instant.now();
    }

    // Getters & setters

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(long requestCount) {
        this.requestCount = requestCount;
    }

    public long getTokenCount() {
        return tokenCount;
    }

    public void setTokenCount(long tokenCount) {
        this.tokenCount = tokenCount;
    }

    public Instant getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Instant lastUsed) {
        this.lastUsed = lastUsed;
    }

    // Helpers to bump counts
    public void incrementRequestCount() {
        this.requestCount++;
    }

    public void incrementTokenCount(long tokens) {
        this.tokenCount += tokens;
    }
}
