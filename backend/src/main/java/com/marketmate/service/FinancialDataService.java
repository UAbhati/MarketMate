package com.marketmate.service;

import com.marketmate.entity.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FinancialDataService {

    private static final Pattern COMPANY_REGEX = Pattern.compile("for\\s+([A-Za-z0-9 &]+)", Pattern.CASE_INSENSITIVE);

    public List<ChatMessage> getContext(String prompt) {
        List<ChatMessage> ctx = new ArrayList<>();

        // crude extraction of “company” from user prompt
        Matcher m = COMPANY_REGEX.matcher(prompt);
        if (m.find()) {
            String company = m.group(1).trim();

            // mock news
            String news = getFinancialNews(company);
            ctx.add(new ChatMessage(null, "function",
                    "Latest news for " + company + ": " + news));

            // mock quarterly results
            String results = getQuarterlyResults(company, "Q1 2025");
            ctx.add(new ChatMessage(null, "function",
                    "Quarterly results for " + company + ": " + results));
        }

        return ctx;
    }

    public String getFinancialNews(String companyName) {
        // … your existing mock or real call …
        return "Mocked news about " + companyName;
    }

    public String getQuarterlyResults(String companyName, String quarter) {
        // … your existing mock or real call …
        return "Mocked results for " + companyName + " in " + quarter;
    }
}
