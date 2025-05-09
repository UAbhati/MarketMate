package com.marketmate.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.marketmate.entity.UsageRecord;
import com.marketmate.repository.UsageRecordRepository;

@Service
public class UsageRecordService {
    private final UsageRecordRepository repo;

    public UsageRecordService(UsageRecordRepository repo) {
        this.repo = repo;
    }

    /**
     * Record a single request:
     * - finds or creates a UsageRecord for (userId, model)
     * - sets the tier if newly created
     * - increments request count and token count
     * - updates lastUsed timestamp
     */
    public void recordUsage(String userId, String model, String tier, int promptTokens, int completionTokens) {
        UsageRecord u = repo.findByUserIdAndModel(userId, model)
                .orElseGet(() -> new UsageRecord(userId, model, tier));

        // Update tier in case it changed
        u.setTier(tier);

        // Increment request and tokens
        u.incrementRequestCount();
        u.incrementTokenCount(promptTokens + completionTokens);

        // Update last used timestamp
        u.setLastUsed(Instant.now());

        repo.save(u);
    }
}
