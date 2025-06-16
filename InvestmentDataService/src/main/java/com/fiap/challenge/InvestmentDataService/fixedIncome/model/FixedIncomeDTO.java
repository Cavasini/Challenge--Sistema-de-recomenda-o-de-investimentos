package com.fiap.challenge.InvestmentDataService.fixedIncome.model;

import java.time.LocalDate;

public record FixedIncomeDTO(
        String name,
        String type,
        double indexerRate,
        String indexer,
        boolean isTaxExempt,
        boolean dailyLiquidity,
        LocalDate maturityDate,
        double minimumInvestmentAmount,
        String issuer,
        double issuerRiskScore,
        String source
) { }
