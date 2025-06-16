package com.fiap.challenge.RecommenderService.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.challenge.RecommenderService.client.dto.InvestmentDataResponse;
import com.fiap.challenge.RecommenderService.client.dto.StockDetailResponse;
import com.fiap.challenge.RecommenderService.client.dto.VariableIncomeResponse;
import com.fiap.challenge.RecommenderService.model.InvestmentProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class InvestmentDataServiceClient {

    private static final Logger log = LoggerFactory.getLogger(InvestmentDataServiceClient.class);

    private String brapiBaseUrl = "http://localhost:8082/api";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public InvestmentDataServiceClient(RestTemplate restTemplate, ObjectMapper objectMapper){
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }


    public Optional<InvestmentDataResponse> getFixedIncome() {

        String url = UriComponentsBuilder.fromUriString(brapiBaseUrl)
                .path("/v1/fixedIncome")
                .toUriString();

        System.out.println(url);

        return executeGetRequest(url, InvestmentDataResponse.class)
                .map(response -> {
                    if (response != null) {
                        return response;
                    }
                    return null;
                });
    }


//    public Optional<VariableIncomeResponse> getVariableIncomesByProfile(InvestmentProfile investmentProfile) {
//
//        String path = switch (investmentProfile) {
//            case InvestmentProfile.CONSERVATIVE -> "/v1/variable/conservative";
//            case InvestmentProfile.MODERATE -> "/v1/variable/moderate";
//            case InvestmentProfile.AGGRESSIVE -> "/v1/variable/aggressive";
//        };
//
//        String url = UriComponentsBuilder.fromUriString(brapiBaseUrl)
//                .path(path)
//                .toUriString();
//
//        System.out.println(url);
//
//        return executeGetRequest(url, VariableIncomeResponse.class)
//                .filter(response -> response.variableIncomesList() != null && !response.variableIncomesList().isEmpty());
//    }

    private <T> Optional<T> executeGetRequest(String url, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Optional.ofNullable(objectMapper.readValue(response.getBody(), responseType));
            } else {
                log.error("Erro ao buscar dados da Brapi API. Status: {}, Corpo: {}", response.getStatusCode(), response.getBody());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Exceção ao fazer requisição para Brapi API para URL {}: {}", url, e.getMessage(), e);
            return Optional.empty();
        }
    }


    public Optional<List<String>> getVariableIncomesByProfile(InvestmentProfile investmentProfile) {

        String path = switch (investmentProfile) {
            case CONSERVATIVE -> "/v1/variable/conservative";
            case MODERATE -> "/v1/variable/moderate";
            case AGGRESSIVE -> "/v1/variable/aggressive";
        };

        String url = UriComponentsBuilder.fromUriString(brapiBaseUrl)
                .path(path)
                .toUriString();

        log.info("Requisição de rendas variáveis para URL: {}", url); // Use log em vez de System.out.println

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<String> variableIncomeSymbols = objectMapper.readValue(response.getBody(), new TypeReference<List<String>>() {});
                return Optional.of(variableIncomeSymbols);
            } else {
                log.error("Erro ao buscar rendas variáveis por perfil. Status: {}, Corpo: {}", response.getStatusCode(), response.getBody());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Exceção ao fazer requisição para Brapi API para URL {}: {}", url, e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Optional<StockDetailResponse> getVariableDetailsBySymbols(List<String> symbols) {
        String url = UriComponentsBuilder.fromUriString(brapiBaseUrl)
                .path("/v1/variable/details")
                .toUriString();

        log.info("Requisição POST para detalhes de variáveis para URL: {}", url);
        log.debug("Enviando símbolos para detalhes: {}", symbols);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<String>> requestEntity = new HttpEntity<>(symbols, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Optional.ofNullable(objectMapper.readValue(response.getBody(), StockDetailResponse.class));
            } else {
                log.error("Erro ao buscar detalhes de variáveis. Status: {}, Corpo: {}", response.getStatusCode(), response.getBody());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Exceção ao fazer requisição POST para URL {}: {}", url, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
