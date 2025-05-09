package com.marketmate.util;

import java.util.List;

public class FinancialRelatedQuestions {
    private static final List<String> FINANCE_KEYWORDS = List.of(
            "finance", "financial", "investment", "investor", "company", "firm", "business", "corporation", "market",
            "economy", "economic", "macroeconomic", "stock", "share", "equity", "ipo", "dividends", "stock split",
            "ticker",
            "market cap", "public company", "listed", "NSE", "BSE", "Sensex", "Nifty", "quarterly result", "earnings",
            "profit",
            "loss", "revenue", "income", "balance sheet", "p&l", "cash flow", "EBITDA", "EPS", "net profit",
            "net income",
            "margin", "expense", "valuation", "pe ratio", "pb ratio", "risk", "volatility", "downgrade",
            "credit rating",
            "outlook", "performance", "returns", "analyst call", "forecast", "projection", "acquisition", "merger",
            "spin-off",
            "bonus", "rights issue", "restructuring", "results announcement", "press release", "stock news",
            "company update",
            "regulatory filing", "SEBI");
    
    public boolean isFinancialQuery(String message) {
        String msgLower = message.toLowerCase();
        return FINANCE_KEYWORDS.stream().anyMatch(msgLower::contains);
    }
}
