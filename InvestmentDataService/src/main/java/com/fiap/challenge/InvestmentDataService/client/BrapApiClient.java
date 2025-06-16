package com.fiap.challenge.InvestmentDataService.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.challenge.InvestmentDataService.client.brapi_dto.*;
import com.fiap.challenge.InvestmentDataService.variableIncome.model.dto.StockDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BrapApiClient {

    private static final Logger log = LoggerFactory.getLogger(BrapApiClient.class);
    private static final DateTimeFormatter API_REQUEST_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String DEFAULT_STOCK_LIMIT = "100";
    private static final String DEFAULT_STOCK_PAGE = "1";
    private static final String BRAZIL_COUNTRY_CODE = "brazil";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

//    @Value("${brapi.api.base-url:https://brapi.dev/api}")
    private String brapiBaseUrl = "https://brapi.dev/api";

//    @Value("${brapi.api.auth-token}")
    private String authorizationHeaderValue = "9cPmBnJyZmvKF6UNGqxYFp";

    public BrapApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<StockDTO> getStockList(String sortBy, String sortOrder, String type) {
        String url = UriComponentsBuilder.fromUriString(brapiBaseUrl)
                .path("/quote/list")
                .queryParam("sortBy", sortBy)
                .queryParam("sortOrder", sortOrder)
                .queryParam("limit", DEFAULT_STOCK_LIMIT)
                .queryParam("page", DEFAULT_STOCK_PAGE)
                .queryParam("type", type)
                .toUriString();

        return executeGetRequest(url, BrapiApiResponseStock.class)
                .map(BrapiApiResponseStock::getStocks)
                .orElseGet(Collections::emptyList);
    }

    public Optional<InflationDataDTO> getInflation() {
        LocalDate todayDate = LocalDate.now();
        LocalDate previousMonthDate = todayDate.minusMonths(1);

        String previousMonthFormatted = previousMonthDate.format(API_REQUEST_DATE_FORMATTER);
        String todayFormatted = todayDate.format(API_REQUEST_DATE_FORMATTER);

        String url = UriComponentsBuilder.fromUriString(brapiBaseUrl)
                .path("/v2/inflation")
                .queryParam("country", BRAZIL_COUNTRY_CODE)
                .queryParam("historical", "True")
                .queryParam("start", previousMonthFormatted)
                .queryParam("end", todayFormatted)
                .toUriString();

        return executeGetRequest(url, BrapApiResponseInflation.class)
                .flatMap(response -> response.data() != null && !response.data().isEmpty()
                        ? Optional.of(response.data().getFirst())
                        : Optional.empty());
    }

    public Optional<PrimeRateDataDTO> getPrimeRate() {
        LocalDate todayDate = LocalDate.now();
        String todayFormatted = todayDate.format(API_REQUEST_DATE_FORMATTER);

        String url = UriComponentsBuilder.fromUriString(brapiBaseUrl)
                .path("/v2/prime-rate")
                .queryParam("country", BRAZIL_COUNTRY_CODE)
                .queryParam("historical", "True")
                .queryParam("start", todayFormatted)
                .queryParam("end", todayFormatted)
                .toUriString();

        return executeGetRequest(url, BrapApiResponsePrimeRate.class)
                .flatMap(response -> response.data() != null && !response.data().isEmpty()
                        ? Optional.of(response.data().getFirst())
                        : Optional.empty());
    }

    public Optional<StockResponseDto> getDetailsOfStocks(List<String> stocks) {
        String formattedStocks = String.join(",", stocks);

        String url = UriComponentsBuilder.fromUriString(brapiBaseUrl)
                .path("/quote/" + formattedStocks)
                .queryParam("range", "1y")
                .queryParam("interval", "3mo")
                .queryParam("fundamental", "false")
                .queryParam("dividends", "false")
                .queryParam("modules", "balanceSheetHistory")
                .toUriString();

        return executeGetRequest(url, StockResponseDto.class);
    }

    private <T> Optional<T> executeGetRequest(String url, Class<T> responseType) {
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
                return Optional.ofNullable(objectMapper.readValue(response.getBody(), responseType));
            } else {
                log.error("Failed to fetch data from Brapi API. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Exception occurred while making request to Brapi API for URL {}: {}", url, e.getMessage(), e);
            return Optional.empty();
        }
    }
}