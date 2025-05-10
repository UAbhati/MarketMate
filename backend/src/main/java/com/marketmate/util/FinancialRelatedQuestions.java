package com.marketmate.util;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class FinancialRelatedQuestions {
    private static final List<String> FINANCE_KEYWORDS = List.of(
            "finance", "financial", "investment", "investor", "investing", "capital",
            "company", "firm", "business", "corporation", "startup", "enterprise",
            "market", "markets", "economy", "economic", "macroeconomic", "microeconomic",
            "stock", "stocks", "share", "equity", "ipo", "fpo", "dividends", "stock split",
            "ticker", "market cap", "public company", "listed", "NSE", "BSE", "Sensex", "Nifty",
            "quarterly result", "quarterly earnings", "earnings", "profit", "loss", "revenue",
            "income", "balance sheet", "p&l", "cash flow", "EBITDA", "EPS", "net profit", "net income",
            "operating margin", "gross margin", "expense", "valuation", "pe ratio", "pb ratio", "price to book",
            "debt", "leverage", "liabilities", "assets", "risk", "volatility", "credit risk", "downgrade",
            "upgrade", "credit rating", "outlook", "guidance", "returns", "performance", "projection", "forecast",
            "analyst", "analyst call", "transcript", "report", "merger", "acquisition", "takeover", "buyout",
            "spin-off", "demerger", "bonus", "rights issue", "fundraising", "venture capital", "private equity",
            "angel investor", "series a", "series b", "funding round", "valuation round",
            "press release", "regulatory filing", "SEBI", "RBI", "FDI", "GDP", "inflation", "interest rate",
            "repo rate", "monetary policy", "fiscal deficit", "foreign exchange", "currency", "exchange rate",
            "hedge", "derivative", "option", "futures", "bond", "treasury", "yield", "mutual fund", "index fund",
            "ETF", "bull market", "bear market", "short selling", "long position", "stop loss", "portfolio",
            "diversification", "liquidity", "layoff", "hiring freeze", "restructuring", "stock news",
            "company update", "results announcement", "annual report");
    
    public boolean isFinancialQuery(String message) {
        String msgLower = message.toLowerCase();
        return FINANCE_KEYWORDS.stream().anyMatch(msgLower::contains);
    }
}
