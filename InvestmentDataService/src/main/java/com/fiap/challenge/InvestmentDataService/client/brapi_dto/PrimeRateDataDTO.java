package com.fiap.challenge.InvestmentDataService.client.brapi_dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PrimeRateDataDTO(
        @JsonProperty("date") String date,
        @JsonProperty("value") String value,
        @JsonProperty("epochDate") Long epochDate
) {}
