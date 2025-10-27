package com.fiap.challenge.RecommenderService.client.dto;

import com.fiap.challenge.RecommenderService.model.InvestimentRecommendation;

import java.time.LocalDate;

public record FixedIncomeDTO(
        String name,
        String type,
        double indexerRate,
        String indexer,
        boolean isTaxExempt,
        boolean dailyLiquidity,
        LocalDate maturityDate, // <--- Agora é String
        Double minimumInvestmentAmount,
        String issuer,
        Integer issuerRiskScore,
        String source
) implements InvestimentRecommendation {}
