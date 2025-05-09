package com.marketmate.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RateLimitService {
    public enum Tier {
        FREE, TIER_1, TIER_2, TIER_3
    }

    private final Map<String, Tier> userTiers = new HashMap<>();

    public Tier getUserTier(String userId) {
        return userTiers.getOrDefault(userId, Tier.FREE);
    }

    public void setUserTier(String userId, Tier tier) {
        userTiers.put(userId, tier);
    }

    public boolean isRateLimitExceeded(String userId) {
        // Basic mock rate limit check
        Tier tier = getUserTier(userId);
        return false; // Assume no limit exceeded for now
    }

    public void checkLimits(String userId, String model, String tier, String prompt) {
        // lookup UsageRecord, enforce RPM/TPM, or throw
        // e.g.
        // if (tooMany) throw new RateLimitExceededException("...");
    }    
}
