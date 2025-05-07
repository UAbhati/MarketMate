package com.marketmate.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import java.time.LocalDateTime;

@Entity
@Table(name = "usage_record")
public class UsageRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String model;

    private int requestCount;
    private int tokenCount;
    private LocalDateTime lastUpdated;

    // Getters and setters
}
