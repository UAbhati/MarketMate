package com.marketmate.service;

import java.util.ArrayList;
import java.util.Collections;
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
        List<ChatMessage> ctx = new ArrayList<>();
        // e.g. extract company tickers from prompt...
        // ctx.add(new ChatMessage(null, "system", getFinancialNews(...)));
        // ctx.add(new ChatMessage(null, "system", getQuarterlyResults(...)));
        return ctx;
    }
}
