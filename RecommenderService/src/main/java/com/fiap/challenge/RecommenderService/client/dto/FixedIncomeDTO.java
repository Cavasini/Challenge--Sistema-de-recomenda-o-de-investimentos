package com.fiap.challenge.RecommenderService.client.dto;

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
) {}
