package com.marketmate.entity;

import javax.persistence.*;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

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

    private LocalDate date; // resets daily
    private int requestsToday;
    private int tokensToday;
    private int minuteOfDay; // e.g. hour*60 + minute
    private int tokensThisMinute;

    // No‐arg constructor for JPA
    public UsageRecord() {}

    // Convenience constructor
    public UsageRecord(String userId, String model, String tier) {
        this.userId = userId;
        this.model = model;
        this.tier = tier;
        this.requestCount = 0;
        this.tokenCount = 0;
        this.lastUsed = Instant.now();
    }

    public interface UsageRecordRepository extends JpaRepository<UsageRecord,Long> {
        Optional<UsageRecord> findByUserIdAndModelName(String userId, String modelName);
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getRequestsToday() {
        return requestsToday;
    }

    public void setRequestsToday(int requestsToday) {
        this.requestsToday = requestsToday;
    }

    public int getTokensToday() {
        return tokensToday;
    }

    public void setTokensToday(int tokensToday) {
        this.tokensToday = tokensToday;
    }
    
    public int getMinuteOfDay() {
        return minuteOfDay;
    }

    public void setMinuteOfDay(int minuteOfDay) {
        this.minuteOfDay = minuteOfDay;
    }
    
    public int getTokensThisMinute() {
        return tokensThisMinute;
    }

    public void setTokensThisMinute(int tokensThisMinute) {
        this.tokensThisMinute = tokensThisMinute;
    }
    // Helpers to bump counts
    public void incrementRequestCount() {
        this.requestCount++;
    }

    public void incrementTokenCount(long tokens) {
        this.tokenCount += tokens;
    }
}
