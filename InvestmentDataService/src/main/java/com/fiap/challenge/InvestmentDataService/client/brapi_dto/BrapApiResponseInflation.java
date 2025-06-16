package com.fiap.challenge.InvestmentDataService.client.brapi_dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record BrapApiResponseInflation(@JsonProperty("inflation") List<InflationDataDTO> data) {

}


