package com.marketmate.service;

import org.springframework.stereotype.Service;

@Service
public class UsageTracker {
    public void recordUsage(String userId, String model, String tier, String prompt, String reply) {
        // Stub for usage logging (token count etc.)
    }
}
