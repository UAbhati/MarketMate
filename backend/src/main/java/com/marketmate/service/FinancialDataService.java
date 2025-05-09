package com.marketmate.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.marketmate.entity.ChatMessage;

@Service
public class FinancialDataService {
    public String getFinancialNews(String companyName) {
        return "Mocked news for company: " + companyName;
    }

    public String getQuarterlyResults(String companyName, String quarter) {
        return "Mocked financial results for " + companyName + " in " + quarter;
    }

    public List<ChatMessage> getContext(String prompt) {
        return List.of(new ChatMessage(null, "data", "NIFTY is up 1.2% today."));
    }
}
