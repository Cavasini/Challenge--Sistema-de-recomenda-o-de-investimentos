package com.fiap.challenge.InvestmentDataService.client.brapi_dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record BrapApiResponsePrimeRate(@JsonProperty("prime-rate") List<PrimeRateDataDTO> data) {

}


