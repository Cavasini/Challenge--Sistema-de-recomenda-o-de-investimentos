package com.fiap.challenge.InvestmentDataService.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.challenge.InvestmentDataService.model.dto.BrapiResponse;
import com.fiap.challenge.InvestmentDataService.model.dto.StockDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BrapClient {

//    private final RestTemplate restTemplate;
//    private final ObjectMapper objectMapper;
private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


//    @Value("https://brapi.dev/api")
    private String brapiBaseUrl = "https://brapi.dev/api";

//    @Value("9cPmBnJyZmvKF6UNGqxYFp")
    private String authorizationHeaderValue = "";

//    public BrapClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
//        this.restTemplate = restTemplate;
//        this.objectMapper = objectMapper;
//    }

    public Set<StockDTO> searchConservative() {
        Set<StockDTO> result = new HashSet<>();
        result.addAll(filterStocks(searchList("market_cap_basic", "desc"), InvestmentProfile.CONSERVATIVE));
        result.addAll(filterStocks(searchList("volume", "desc"), InvestmentProfile.CONSERVATIVE));
        return result;
    }

    public Set<StockDTO> searchModerate() {
        Set<StockDTO> result = new HashSet<>();
        result.addAll(filterStocks(searchList("market_cap_basic", "desc"), InvestmentProfile.MODERATE));
        result.addAll(filterStocks(searchList("change", "desc"), InvestmentProfile.MODERATE));
        result.addAll(filterStocks(searchList("volume", "desc"), InvestmentProfile.MODERATE));
        return result;
    }

    public Set<StockDTO> searchAggressive() {
        Set<StockDTO> result = new HashSet<>();
        result.addAll(filterStocks(searchList("change", "desc"), InvestmentProfile.AGGRESSIVE));
        result.addAll(filterStocks(searchList("volume", "desc"), InvestmentProfile.AGGRESSIVE));
        result.addAll(filterStocks(searchList("market_cap_basic", "asc"), InvestmentProfile.AGGRESSIVE));
        return result;
    }

    private List<StockDTO> searchList(String sortBy, String sortOrder) {
        String url = UriComponentsBuilder.fromUriString(brapiBaseUrl)
                .path("/quote/list")
                .queryParam("sortBy", sortBy)
                .queryParam("sortOrder", sortOrder)
                .queryParam("limit", 120)
                .queryParam("page", 1)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationHeaderValue);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                BrapiResponse brapiResponse = objectMapper.readValue(response.getBody(), BrapiResponse.class);
                return brapiResponse.getStocks();
            } else {
                System.err.println("Erro ao buscar dados da Brapi API. Status: " + response.getStatusCode() + ", Corpo: " + response.getBody());
                return Collections.emptyList();
            }
        } catch (Exception e) {
            System.err.println("Exceção ao fazer requisição para Brapi API: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private List<StockDTO> filterStocks(List<StockDTO> stockDTOList, InvestmentProfile profile) {
        return stockDTOList.stream()
                .filter(stockDTO -> stockDTO.getMarketCap() != null)
                .filter(stockDTO -> {
                    switch (profile) {
                        case CONSERVATIVE:
                            return stockDTO.getMarketCap() > 5_000_000_000L &&
                                    stockDTO.getVolume() > 500_000 &&
                                    stockDTO.getChange() >= -2 && stockDTO.getChange() <= 2 &&
                                    List.of("Finance", "Utilities", "Consumer Non-Durables", "Health Services", "Energy Minerals").contains(stockDTO.getSector()) &&
                                    List.of("stock", "fund").contains(stockDTO.getType());
                        case MODERATE:
                            return stockDTO.getMarketCap() > 2_000_000_000L &&
                                    stockDTO.getVolume() > 300_000 &&
                                    stockDTO.getChange() >= -3 && stockDTO.getChange() <= 5 &&
                                    List.of("Industrial Services", "Technology Services", "Consumer Services", "Health Technology", "Non-Energy Minerals", "Retail Trade").contains(stockDTO.getSector()) &&
                                    List.of("stock", "fund", "bdr").contains(stockDTO.getType());
                        case AGGRESSIVE:
                            return stockDTO.getMarketCap() != null &&
                                    stockDTO.getVolume() > 100_000 &&
                                    stockDTO.getChange() >= -2 && stockDTO.getChange() <= 10 &&
                                    List.of(
                                            "Electronic Technology", "Technology Services", "Communications",
                                            "Consumer Durables", "Process Industries", "Producer Manufacturing",
                                            "Miscellaneous", "Retail Trade", "Health Technology"
                                    ).contains(stockDTO.getSector()) &&
                                    List.of("stock", "bdr", "fund").contains(stockDTO.getType());
                        default:
                            return false;
                    }
                })
                .collect(Collectors.toList());
    }

    private enum InvestmentProfile {
        CONSERVATIVE,
        MODERATE,
        AGGRESSIVE
    }
}
