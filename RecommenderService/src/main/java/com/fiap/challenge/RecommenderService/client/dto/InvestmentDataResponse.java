package com.fiap.challenge.RecommenderService.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record InvestmentDataResponse(
        @JsonProperty("fixedIncomeDTOSList")
        List<FixedIncomeDTO> fixedIncomeList,
        String inflation,
        String primeRate
) {}
