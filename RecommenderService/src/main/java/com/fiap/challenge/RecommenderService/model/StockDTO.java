package com.fiap.challenge.RecommenderService.model;

import java.util.List;

public record StockDTO(String ticker, double currentPrice, double previousPrice, double earningsPerShare,
                       double marketCap, double equity, double fiftyTwoWeekLow, double fiftyTwoWeekHigh,
                       double currentVolume, List<Double> priceHistory, List<Long> volumeHistory,
                       String longName, String currency, String logoUrl, Double regularMarketChange, Double regularMarketChangePercent) {
}
