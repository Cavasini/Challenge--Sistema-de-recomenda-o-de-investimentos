package com.fiap.challenge.InvestmentDataService.client.brapi_dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fiap.challenge.InvestmentDataService.variableIncome.model.dto.StockDTO;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BrapiApiResponseStock {
    private List<StockDTO> stocks;

    public List<StockDTO> getStocks() {
        return stocks;
    }

    public void setStocks(List<StockDTO> stocks) {
        this.stocks = stocks;
    }
}
