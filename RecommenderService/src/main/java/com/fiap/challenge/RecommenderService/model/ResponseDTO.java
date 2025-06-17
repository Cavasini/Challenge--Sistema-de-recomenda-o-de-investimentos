package com.fiap.challenge.RecommenderService.model;

import com.fiap.challenge.RecommenderService.client.dto.FixedIncomeDTO;

import java.util.List;

public record ResponseDTO(List<FixedIncomeDTO> FixedIncomesList, List<StockScoreResult> VariableIncomesList) {
}
