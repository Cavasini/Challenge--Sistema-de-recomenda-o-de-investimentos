package com.fiap.challenge.InvestmentDataService.client.brapi_dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InflationDataDTO(
        @JsonProperty("date") String date, // Sem as anotações de serializador/desserializador customizado
        @JsonProperty("value") String value,
        @JsonProperty("epochDate") Long epochDate
) {}
