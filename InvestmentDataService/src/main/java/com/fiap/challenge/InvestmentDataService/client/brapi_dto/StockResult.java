package com.fiap.challenge.InvestmentDataService.client.brapi_dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public record StockResult(
        String currency,
        Double marketCap,
        String shortName,
        String longName,
        Double regularMarketChange,
        Double regularMarketChangePercent,
        OffsetDateTime regularMarketTime,
        Double regularMarketPrice,
        Double regularMarketDayHigh,
        String regularMarketDayRange,
        Double regularMarketDayLow,
        Long regularMarketVolume,
        Double regularMarketPreviousClose,
        Double regularMarketOpen,
        String fiftyTwoWeekRange,
        Double fiftyTwoWeekLow,
        Double fiftyTwoWeekHigh,
        String symbol,
        String logourl,
        String usedInterval,
        String usedRange,
        List<HistoricalDataPrice> historicalDataPrice,
        List<String> validRanges,
        List<String> validIntervals,

        Object balanceSheetHistory,
        Double priceEarnings,
        Double earningsPerShare
) {
    @JsonIgnore
    public List<BalanceSheetStatement> getParsedBalanceSheetHistory() {
        if (balanceSheetHistory instanceof List<?>) {
            return (List<BalanceSheetStatement>) balanceSheetHistory;
        } else if (balanceSheetHistory instanceof Map<?, ?>) {
            Map<String, ?> map = (Map<String, ?>) balanceSheetHistory;
            if (map.containsKey("balanceSheetStatements")) {

                return (List<BalanceSheetStatement>) map.get("balanceSheetStatements");
            }
        }
        return List.of(); // Retorna uma lista vazia se não for nenhum dos tipos esperados
    }
}
