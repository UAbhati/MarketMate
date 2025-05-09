package com.marketmate.service;

import com.google.common.util.concurrent.RateLimiter;
import com.marketmate.util.RateLimitExceededException;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RateLimitService {
    public enum Tier {
        FREE(3), TIER_1(500), TIER_2(5000), TIER_3(50000);

        private final int rpm;

        Tier(int rpm) {
            this.rpm = rpm;
        }

        public int getRpm() {
            return rpm;
        }
    }

    private final Map<String, Tier> userTiers = new ConcurrentHashMap<>();
    private final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();

    public Tier getUserTier(String userId) {
        return userTiers.getOrDefault(userId, Tier.FREE);
    }

    public void setUserTier(String userId, Tier tier) {
        userTiers.put(userId, tier);
        // reset their limiter when tier changes:
        limiters.remove(userId);
    }

    public boolean isRateLimitExceeded(String userId) {
        // Basic mock rate limit check
        Tier tier = getUserTier(userId);
        return false; // Assume no limit exceeded for now
    }

    /** Throws if the user has exhausted their RPM allowance. */
    public void checkRateLimit(String userId) {
        Tier tier = getUserTier(userId);
        // one RateLimiter per user
        RateLimiter rl = limiters.computeIfAbsent(userId, id ->
            // convert RPM → permits per second:
            RateLimiter.create(tier.getRpm() / 60.0)
        );
        if (!rl.tryAcquire()) {
            throw new RateLimitExceededException(
              "Rate limit exceeded. Allowed " + tier.getRpm() + " requests per minute."
            );
        }
    }  
}
