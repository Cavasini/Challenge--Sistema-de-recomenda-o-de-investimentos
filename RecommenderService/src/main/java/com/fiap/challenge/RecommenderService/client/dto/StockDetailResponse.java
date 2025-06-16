package com.fiap.challenge.RecommenderService.client.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record StockDetailResponse(
        List<StockResult> results,
        OffsetDateTime requestedAt,
        String took
) {}
