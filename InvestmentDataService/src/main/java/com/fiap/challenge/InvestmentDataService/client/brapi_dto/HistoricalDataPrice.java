package com.fiap.challenge.InvestmentDataService.client.brapi_dto;

public record HistoricalDataPrice(
        Long date,
        Double open,
        Double high,
        Double low,
        Double close,
        Long volume,
        Double adjustedClose
) {}
