package com.marketmate.repository;

import com.marketmate.entity.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsageRecordRepository extends JpaRepository<UsageRecord, Long> {
    Optional<UsageRecord> findByUserIdAndModel(String userId, String model);
}
