package com.marketmate.service;

import com.marketmate.entity.ChatMessage;
import com.marketmate.util.FinancialRelatedQuestions;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FinancialDataService {
    private FinancialRelatedQuestions financialRelatedQuestions;
    private static final Pattern COMPANY_REGEX = Pattern.compile("for\\s+([A-Za-z0-9 &]+)", Pattern.CASE_INSENSITIVE);

    /**
     * Returns mocked financial news and results for a given company only if the
     * prompt
     * is relevant to the financial domain. Returns an empty list otherwise.
     */
    public List<ChatMessage> getContext(String prompt) {
        List<ChatMessage> ctx = new ArrayList<>();

        if (!isFinancialQuery(prompt)) {
            return ctx; // Return empty if not financial
        }

        Matcher m = COMPANY_REGEX.matcher(prompt);
        if (m.find()) {
            String company = m.group(1).trim();

            // mock Financial News API integration
            String news = getFinancialNews(company);
            ctx.add(new ChatMessage(null, "function",
                    "Latest news for " + company + ": " + news));

            // mock Quarterly Results API integration
            String results = getQuarterlyResults(company, "Q1 2025");
            ctx.add(new ChatMessage(null, "function",
                    "Quarterly results for " + company + ": " + results));
        }

        return ctx;
    }

    private boolean isFinancialQuery(String prompt) {
        String lower = prompt.toLowerCase();
        return financialRelatedQuestions.isFinancialQuery(lower);
    }

    /**
     * Simulates calling the Financial News API.
     * Request: { "company_name": ..., "date": ... }
     */
    public String getFinancialNews(String companyName) {
        return "{ \"company_name\": \"" + companyName
                + "\", \"news\": [ { \"headline\": \"Big investment in AI\", \"description\": \"Company announces large AI initiative.\", \"date\": \"2025-05-01\", \"source\": \"Financial Times\" } ] }";
    }

    /**
     * Simulates calling the Quarterly Financial Results API.
     * Request: { "company_name": ..., "quarter": ..., "api_key": ... }
     */
    public String getQuarterlyResults(String companyName, String quarter) {
        return "{ \"company_name\": \"" + companyName + "\", \"quarter\": \"" + quarter
                + "\", \"valuation_ratios\": { \"pe_ratio\": 23.4, \"pb_ratio\": 3.1 }, \"files\": { \"balance_sheet_excel\": \"https://dummyfinancialapi.com/files/balance_sheet.xlsx\", \"analyst_call_transcript_doc\": \"https://dummyfinancialapi.com/files/analyst_call_transcript.docx\" } }";
    }
}
