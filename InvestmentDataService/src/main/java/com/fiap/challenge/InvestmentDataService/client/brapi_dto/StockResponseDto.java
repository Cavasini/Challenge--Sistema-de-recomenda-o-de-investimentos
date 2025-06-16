package com.fiap.challenge.InvestmentDataService.client.brapi_dto;

import java.time.OffsetDateTime;
import java.util.List;

public record StockResponseDto(
        List<StockResult> results,
        OffsetDateTime requestedAt,
        String took
) {}
