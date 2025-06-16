package com.fiap.challenge.RecommenderService.service;

import com.fiap.challenge.RecommenderService.client.InvestmentDataServiceClient;
import com.fiap.challenge.RecommenderService.client.dto.FixedIncomeDTO;
import com.fiap.challenge.RecommenderService.client.dto.InvestmentDataResponse;
import com.fiap.challenge.RecommenderService.model.InvestmentProfile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FixedIncomeAnalyzer {

    private final InvestmentDataServiceClient investmentDataServiceClient;

    public FixedIncomeAnalyzer(InvestmentDataServiceClient investmentDataServiceClient) {
        this.investmentDataServiceClient = investmentDataServiceClient;
    }

    public List<FixedIncomeDTO> getFixedIncomesBasedOnProfile(InvestmentProfile profile) {
        InvestmentDataResponse response = investmentDataServiceClient.getFixedIncome().get();
        return filterBestFixedIncomes(
                response.fixedIncomeList(),
                Double.parseDouble(response.inflation()),
                Double.parseDouble(response.primeRate()),
                profile
        );
    }

    private List<FixedIncomeDTO> filterBestFixedIncomes(List<FixedIncomeDTO> all,
                                                        double inflation,
                                                        double selic,
                                                        InvestmentProfile profile) {

        return all.stream()
                .filter(i -> i.minimumInvestmentAmount() <= 10000)
                .filter(i -> i.issuerRiskScore() != null && i.issuerRiskScore() <= riskLimit(profile))
                .filter(i -> maturityAcceptable(i.maturityDate(), profile))
                .filter(i -> indexerAcceptable(i.indexer(), profile))
                .filter(i -> liquidityAcceptable(i.dailyLiquidity(), profile))
                .sorted((a, b) -> Double.compare(
                        calculateYield(b, inflation, selic),
                        calculateYield(a, inflation, selic)))
                .limit(limitByProfile(profile))
                .collect(Collectors.toList());
    }

    private boolean maturityAcceptable(LocalDate maturityDate, InvestmentProfile profile) {
        if (maturityDate == null) return true; // ETFs ou sem vencimento
        LocalDate now = LocalDate.now();
        long yearsToMaturity = now.until(maturityDate).getYears();
        return switch (profile) {
            case CONSERVATIVE -> yearsToMaturity <= 3;
            case MODERATE -> yearsToMaturity <= 5;
            case AGGRESSIVE -> yearsToMaturity <= 10;
        };
    }

    private boolean liquidityAcceptable(Boolean dailyLiquidity, InvestmentProfile profile) {
        return switch (profile) {
            case CONSERVATIVE -> Boolean.TRUE.equals(dailyLiquidity);
            case MODERATE, AGGRESSIVE -> true;
        };
    }

    private boolean indexerAcceptable(String indexer, InvestmentProfile profile) {
        return switch (profile) {
            case CONSERVATIVE -> List.of("Selic", "CDI").contains(indexer);
            case MODERATE -> List.of("CDI", "IPCA").contains(indexer);
            case AGGRESSIVE -> List.of("IPCA", "Prefixado", "USD Yield").contains(indexer);
        };
    }

    private int riskLimit(InvestmentProfile profile) {
        return switch (profile) {
            case CONSERVATIVE -> 1;
            case MODERATE -> 2;
            case AGGRESSIVE -> 3;
        };
    }

    private int limitByProfile(InvestmentProfile profile) {
        return switch (profile) {
            case CONSERVATIVE -> 3;
            case MODERATE -> 4;
            case AGGRESSIVE -> 5;
        };
    }

    private double calculateYield(FixedIncomeDTO i, double inflation, double selic) {
        return switch (i.indexer()) {
            case "CDI", "Selic" -> (i.indexerRate() / 100.0) * selic;
            case "IPCA" -> inflation + i.indexerRate();
            case "Prefixado" -> i.indexerRate();
            case "USD Yield" -> i.indexerRate();
            default -> 0;
        };
    }
}
