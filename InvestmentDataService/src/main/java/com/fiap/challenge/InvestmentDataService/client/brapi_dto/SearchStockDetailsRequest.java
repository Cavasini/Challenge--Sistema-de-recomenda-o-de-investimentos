package com.fiap.challenge.InvestmentDataService.client.brapi_dto;

import java.util.List;

public record SearchStockDetailsRequest(List<String> stockList) {
}
