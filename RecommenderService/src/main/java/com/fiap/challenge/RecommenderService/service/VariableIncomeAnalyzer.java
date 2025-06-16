package com.fiap.challenge.RecommenderService.service;

import com.fiap.challenge.RecommenderService.client.InvestmentDataServiceClient;
import com.fiap.challenge.RecommenderService.client.dto.InvestmentDataResponse;
import com.fiap.challenge.RecommenderService.client.dto.StockDetailResponse;
import com.fiap.challenge.RecommenderService.client.dto.StockResult;
import com.fiap.challenge.RecommenderService.client.dto.VariableIncomeResponse;
import com.fiap.challenge.RecommenderService.model.InvestmentProfile;
import com.fiap.challenge.RecommenderService.model.StockDTO;
import com.fiap.challenge.RecommenderService.model.StockScoreResult;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VariableIncomeAnalyzer {

    private final InvestmentDataServiceClient investmentDataServiceClient;

    public VariableIncomeAnalyzer(InvestmentDataServiceClient investmentDataServiceClient){
        this.investmentDataServiceClient = investmentDataServiceClient;
    }

    public List<StockScoreResult> getAndProcessGroupedVariableIncomes() {
        Optional<List<String>> optionalVariableIncomes = investmentDataServiceClient.getVariableIncomesByProfile(InvestmentProfile.AGGRESSIVE);

        if (optionalVariableIncomes.isPresent()) {
            List<String> allSymbols = optionalVariableIncomes.get();
            List<List<String>> groupedSymbols = new ArrayList<>();
            final int GROUP_SIZE = 10;

            for (int i = 0; i < allSymbols.size(); i += GROUP_SIZE) {
                int endIndex = Math.min(i + GROUP_SIZE, allSymbols.size());
                groupedSymbols.add(new ArrayList<>(allSymbols.subList(i, endIndex)));
            }

            List<Optional<StockDetailResponse>> responsesForGroups = new ArrayList<>();
            for (List<String> group : groupedSymbols) {
                if (!group.isEmpty()) {
                    Optional<StockDetailResponse> detailsResponse = investmentDataServiceClient.getVariableDetailsBySymbols(group);
                    responsesForGroups.add(detailsResponse);
                }
            }

            List<StockScoreResult> stockScoreList = new ArrayList<>();

            responsesForGroups.stream()
                    .forEach(stockDetailResponse -> stockDetailResponse.get().results().stream()
                            .map(stockResult -> calculateScoreFromMetrics(stockDataMapper(stockResult), InvestmentProfile.AGGRESSIVE))
                            .filter(score -> score.score >= 5.0)
                            .forEach(stockScoreList::add));

            stockScoreList.sort(Comparator.comparingDouble((StockScoreResult stockScore) -> stockScore.score).reversed());

            return stockScoreList;

        } else {
            return Collections.emptyList();
        }
    }

    public StockDTO stockDataMapper(StockResult stockResult){
        return new StockDTO(
                stockResult.symbol() != null ? stockResult.symbol() : "UNKNOWN",
                stockResult.regularMarketPrice() != null ? stockResult.regularMarketPrice() : 0.0,
                stockResult.regularMarketPreviousClose() != null ? stockResult.regularMarketPreviousClose() : 0.0,
                stockResult.earningsPerShare() != null ? stockResult.earningsPerShare() : 0.01,
                stockResult.marketCap() != null ? stockResult.marketCap() : 0.0,
                stockResult.getFirstTotalStockholderEquity() != null ? stockResult.getFirstTotalStockholderEquity() : 0.0,
                stockResult.fiftyTwoWeekLow() != null ? stockResult.fiftyTwoWeekLow() : 0.0,
                stockResult.fiftyTwoWeekHigh() != null ? stockResult.fiftyTwoWeekHigh() : 0.0,
                stockResult.regularMarketVolume() != null ? stockResult.regularMarketVolume() : 0L,
                stockResult.getPriceHistory() != null ? stockResult.getPriceHistory() : new ArrayList<>(),
                stockResult.getVolumeHistory() != null ? stockResult.getVolumeHistory() : new ArrayList<>(),
                stockResult.longName(),
                stockResult.currency(),
                stockResult.logourl(),
                stockResult.regularMarketChange(),
                stockResult.regularMarketChangePercent()
        );
    }

    public static double calculateReturn(double currentPrice, double pastPrice) {
        return (currentPrice - pastPrice) / pastPrice;
    }

    public static double calculateVolatility(List<Double> prices) {
        double average = prices.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = prices.stream().mapToDouble(p -> Math.pow(p - average, 2)).average().orElse(0);
        return Math.sqrt(variance);
    }

    public static double calculateMaxDrawdown(List<Double> prices) {
        double peak = prices.get(0);
        double maxDrawdown = 0;
        for (double price : prices) {
            if (price > peak) peak = price;
            double drawdown = (peak - price) / peak;
            maxDrawdown = Math.max(maxDrawdown, drawdown);
        }
        return maxDrawdown;
    }

    public static double calculatePE(double price, double eps) {
        return price / eps;
    }

    public static double calculatePB(double price, double equity, double marketCap) {
        double shares = marketCap / price;
        double bookValuePerShare = equity / shares;
        return price / bookValuePerShare;
    }

    public static double calculateRSI(List<Double> prices, int period) {
        double gain = 0, loss = 0;
        for (int i = 1; i < period && i < prices.size(); i++) {
            double delta = prices.get(i) - prices.get(i - 1);
            if (delta > 0) gain += delta;
            else loss -= delta;
        }
        if (loss == 0) return 100;
        double rs = gain / loss;
        return 100 - (100 / (1 + rs));
    }

    public static double calculateAverageVolume(List<Long> volumes) {
        return volumes.stream().mapToDouble(Long::doubleValue).average().orElse(0);
    }

    public static double calculatePositionInRange(double currentPrice, double min52, double max52) {
        return (currentPrice - min52) / (max52 - min52);
    }

    public static StockScoreResult calculateScoreFromMetrics(StockDTO dto, InvestmentProfile profile) {
        double pe = calculatePE(dto.currentPrice(), dto.earningsPerShare());
        double pb = calculatePB(dto.currentPrice(), dto.equity(), dto.marketCap());
        double volatility = calculateVolatility(dto.priceHistory());
        double ret = calculateReturn(dto.currentPrice(), dto.previousPrice());
        double drawdown = calculateMaxDrawdown(dto.priceHistory());
        double rsi = calculateRSI(dto.priceHistory(), 14);
        double rangePosition = calculatePositionInRange(dto.currentPrice(), dto.fiftyTwoWeekLow(), dto.fiftyTwoWeekHigh());
        double avgVolume = calculateAverageVolume(dto.volumeHistory());

        switch (profile) {
            case CONSERVATIVE -> {
                return calculateScoreConservative(dto);
            }
            case MODERATE -> {
                return calculateScoreModerate(dto);
            }
            case AGGRESSIVE -> {
                return calculateScoreAggressive(dto);
            }
            default -> throw new IllegalArgumentException("Perfil não reconhecido");
        }
    }

    public static StockScoreResult calculateScoreConservative(StockDTO dto) {
        double score = 0;
        StringBuilder justification = new StringBuilder();

        if (calculatePE(dto.currentPrice(), dto.earningsPerShare()) < 15) {
            score += 2;
            justification.append("P/L abaixo de 15. ");
        }
        if (calculatePB(dto.currentPrice(), dto.equity(), dto.marketCap()) < 2) {
            score += 1.5;
            justification.append("P/VPA abaixo de 2. ");
        }
        if (calculateVolatility(dto.priceHistory()) < 0.05) {
            score += 1.5;
            justification.append("Volatilidade muito baixa. ");
        }
        if (calculateMaxDrawdown(dto.priceHistory()) < 0.2) {
            score += 1;
            justification.append("Baixo drawdown. ");
        }
        if (dto.currentVolume() > calculateAverageVolume(dto.volumeHistory()) * 0.8) {
            score += 1;
            justification.append("Boa liquidez. ");
        }

        return new StockScoreResult(dto.ticker(),dto.longName(),dto.currency(),dto.logoUrl(),dto.currentPrice(), dto.regularMarketChange(),dto.regularMarketChangePercent(), Math.min(score * 1.5, 10));
    }


    public static StockScoreResult calculateScoreModerate(StockDTO dto) {
        double score = 0;
        StringBuilder justification = new StringBuilder();

        if (calculatePE(dto.currentPrice(), dto.earningsPerShare()) < 20) {
            score += 1.5;
            justification.append("P/L atrativo para crescimento. ");
        }
        if (calculateReturn(dto.currentPrice(), dto.previousPrice()) > 0.01) {
            score += 1.5;
            justification.append("Retorno positivo. ");
        }
        if (calculateVolatility(dto.priceHistory()) < 0.1) {
            score += 1;
            justification.append("Volatilidade moderada. ");
        }
        if (calculateMaxDrawdown(dto.priceHistory()) < 0.3) {
            score += 1;
            justification.append("Drawdown aceitável. ");
        }
        if (calculateRSI(dto.priceHistory(), 14) > 30 && calculateRSI(dto.priceHistory(), 14) < 70) {
            score += 1;
            justification.append("RSI em faixa saudável. ");
        }

        return new StockScoreResult(dto.ticker(),dto.longName(),dto.currency(),dto.logoUrl(),dto.currentPrice(), dto.regularMarketChange(),dto.regularMarketChangePercent(), Math.min(score * 1.5, 10));
    }


    public static StockScoreResult calculateScoreAggressive(StockDTO dto) {
        double score = 0;

        if (calculateReturn(dto.currentPrice(), dto.previousPrice()) > 0.02) {
            score += 2;
        }
        if (calculateVolatility(dto.priceHistory()) > 0.1) {
            score += 1.5;
        }
        if (calculateMaxDrawdown(dto.priceHistory()) < 0.4) {
            score += 1;
        }
        if (calculateRSI(dto.priceHistory(), 14) < 30) {
            score += 1;
        }
        if (calculatePositionInRange(dto.currentPrice(), dto.fiftyTwoWeekLow(), dto.fiftyTwoWeekHigh()) < 0.25) {
            score += 1;
        }
        if (calculateAverageVolume(dto.volumeHistory()) > 1_000_000) {
            score += 0.5;
        }

        return new StockScoreResult(dto.ticker(),dto.longName(),dto.currency(),dto.logoUrl(),dto.currentPrice(), dto.regularMarketChange(),dto.regularMarketChangePercent(), Math.min(score * 1.5, 10));
    }

}
