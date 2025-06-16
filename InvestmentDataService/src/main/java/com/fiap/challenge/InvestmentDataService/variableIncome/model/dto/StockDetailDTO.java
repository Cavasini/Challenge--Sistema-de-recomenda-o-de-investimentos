package com.fiap.challenge.InvestmentDataService.variableIncome.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockDetailDTO(String symbol, String sector, Long marketCap, Double priceEarnings, Double earningsPerShare, Double dividendYield,
                             Double returnOnEquity, Double regularMarketChangePercent, Long regularMarketVolume, List<DividendDTO> dividendsData) {

    public boolean hasRecentDividends() {
        return dividendsData != null && !dividendsData.isEmpty();
    }
}
