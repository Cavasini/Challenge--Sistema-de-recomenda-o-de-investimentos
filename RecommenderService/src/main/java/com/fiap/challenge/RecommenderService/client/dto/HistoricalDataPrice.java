package com.fiap.challenge.RecommenderService.client.dto;

public record HistoricalDataPrice(
        Long date,
        Double open,
        Double high,
        Double low,
        Double close,
        Long volume,
        Double adjustedClose
) {}
