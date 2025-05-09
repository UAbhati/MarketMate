package com.marketmate.service;

import com.google.common.util.concurrent.RateLimiter;
import com.marketmate.entity.UsageRecord;
import com.marketmate.repository.UsageRecordRepository;
import com.marketmate.util.RateLimitExceededException;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class RateLimitService {
    public enum Tier {
        FREE(3, 200, 40_000, 1_000_000),
        TIER_1(500, 10_000, 200_000, 5_000_000),
        TIER_2(5_000, 100_000, 2_000_000, 50_000_000),
        TIER_3(50_000, 1_000_000, 20_000_000, 500_000_000);

        private final int rpm, rpd, tpm, tpd;

        Tier(int rpm, int rpd, int tpm, int tpd) {
            this.rpm = rpm;
            this.rpd = rpd;
            this.tpm = tpm;
            this.tpd = tpd;
        }
    }

    private final Map<String, Tier> userTiers = new ConcurrentHashMap<>();
    private final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();

    @Autowired UsageRecordRepository usageRepo;
    
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
        RateLimiter.create(tier.rpm / 60.0));
        if (!rl.tryAcquire()) {
            throw new RateLimitExceededException(
                    "Rate limit exceeded. Allowed " + tier.rpm + " requests per minute.");
        }
    }

    /** Throws if the user has exhausted their RPM allowance. */
    public void checkAllLimits(
        String userId,
        String model,
        int promptTokens,
        int completionTokens
    ) {
        Tier tier = getUserTier(userId);
        // 1) RPM
        RateLimiter rl = limiters.computeIfAbsent(userId,
        id -> RateLimiter.create(tier.rpm/60.0));
        if(!rl.tryAcquire()){
        throw new RateLimitExceededException(
            "Exceeded "+tier.rpm+" requests/minute");
        }

        // 2) RPD, 3) TPM & 4) TPD
        LocalDateTime now   = LocalDateTime.now();
        LocalDate     today = now.toLocalDate();
        int           minuteOfDay = now.getHour()*60 + now.getMinute();
        int           tokens   = promptTokens+completionTokens;

        UsageRecord rec = usageRepo
        .findByUserIdAndModel(userId,model)
        .orElseGet(() -> {
            UsageRecord r = new UsageRecord();
            r.setUserId(userId);
            r.setModel(model);
            r.setDate(today);
            return r;
        });

        // reset daily
        if(!today.equals(rec.getDate())){
        rec.setDate(today);
        rec.setRequestsToday(0);
        rec.setTokensToday(0);
        }
        // reset per‐minute
        if(rec.getMinuteOfDay() != minuteOfDay){
        rec.setMinuteOfDay(minuteOfDay);
        rec.setTokensThisMinute(0);
        }

        // RPD
        if(rec.getRequestsToday()+1 > tier.rpd){
        throw new RateLimitExceededException(
            "Exceeded "+tier.rpd+" requests/day");
        }
        // TPM
        if(rec.getTokensThisMinute()+tokens > tier.tpm){
        throw new RateLimitExceededException(
            "Exceeded "+tier.tpm+" tokens/minute");
        }
        // TPD
        if(rec.getTokensToday()+tokens > tier.tpd){
        throw new RateLimitExceededException(
            "Exceeded "+tier.tpd+" tokens/day");
        }

        // commit counts
        rec.setRequestsToday(rec.getRequestsToday()+1);
        rec.setTokensThisMinute(rec.getTokensThisMinute()+tokens);
        rec.setTokensToday(rec.getTokensToday()+tokens);
        usageRepo.save(rec);
    }
}
