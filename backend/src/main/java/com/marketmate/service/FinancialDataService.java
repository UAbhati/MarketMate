package com.marketmate.service;

import org.springframework.stereotype.Service;

@Service
public class FinancialDataService {
    public String getFinancialNews(String companyName) {
        return "Mocked news for company: " + companyName;
    }

    public String getQuarterlyResults(String companyName, String quarter) {
        return "Mocked financial results for " + companyName + " in " + quarter;
    }
}
