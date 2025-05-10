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
    private final FinancialRelatedQuestions financialRelatedQuestions;
    private static final Pattern COMPANY_REGEX = Pattern.compile("(?:for|of|about)\\s+([A-Za-z0-9.&\\-\\s]+)",
            Pattern.CASE_INSENSITIVE);

    public FinancialDataService(FinancialRelatedQuestions financialRelatedQuestions) {
        this.financialRelatedQuestions = financialRelatedQuestions;
    }

    /**
     * Returns mocked financial news and results for a given company only if the
     * prompt
     * is relevant to the financial domain. Returns an empty list otherwise.
     */
    public List<ChatMessage> getContext(String prompt) {
        List<ChatMessage> ctx = new ArrayList<>();

        String company = extractCompanyName(prompt);
        if (isFinancialQuery(prompt) && hasFinancialIntent(prompt) && extractCompanyName(prompt) != null) {
                
                if (company != null) {
                // mock Financial News API integration
                String news = getFinancialNews(company);
                ctx.add(new ChatMessage(null, "function",
                        "Latest news for " + company + ": " + news));

                // mock Quarterly Results API integration
                String resultsFormatted = formatResults(company);
                ctx.add(new ChatMessage(null, "function", resultsFormatted));
            }
        }

        return ctx;
    }

    private boolean hasFinancialIntent(String prompt) {
        String promptLower = prompt.toLowerCase();
        return promptLower.matches(
                ".*\\b(pe ratio|market value|stock price|quarterly results|valuation|news|revenue|eps|earnings|growth|profit|balance sheet)\\b.*");
    }    

    private String extractCompanyName(String prompt) {
        Matcher matcher = COMPANY_REGEX.matcher(prompt);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
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

    /*
     * formatted results
     */
    private String formatResults(String company) {
        return String.format(
                "📊 *Quarterly Results for %s (Q1 2025)*\n\n" +
                        "- **PE Ratio**: 23.4\n" +
                        "- **PB Ratio**: 3.1\n\n" +
                        "📂 *Reports:*\n" +
                        "- [Balance Sheet (Excel)](https://dummyfinancialapi.com/files/balance_sheet.xlsx)\n" +
                        "- [Analyst Call Transcript (Doc)](https://dummyfinancialapi.com/files/analyst_call_transcript.docx)\n\n"
                        +
                        "_This is a mocked response._",
                company);
    }

}
