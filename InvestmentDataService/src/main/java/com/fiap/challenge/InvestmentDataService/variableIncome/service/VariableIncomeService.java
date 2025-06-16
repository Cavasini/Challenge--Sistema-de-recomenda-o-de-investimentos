package com.fiap.challenge.InvestmentDataService.variableIncome.service;

import com.fiap.challenge.InvestmentDataService.client.BrapApiClient;
import com.fiap.challenge.InvestmentDataService.client.brapi_dto.StockResponseDto;
import com.fiap.challenge.InvestmentDataService.variableIncome.model.InvestmentProfile;
import com.fiap.challenge.InvestmentDataService.variableIncome.model.dto.StockDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Importe Optional
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VariableIncomeService {

    private final BrapApiClient brapApiClient;

    public VariableIncomeService(BrapApiClient brapApiClient) {
        this.brapApiClient = brapApiClient;
    }

    private Set<String> searchEligibleStocksByProfile(InvestmentProfile profile) {
        List<StockDTO> resultList = new ArrayList<>();
        resultList.addAll(brapApiClient.getStockList("market_cap_basic", "desc", "stock"));
        resultList.addAll(brapApiClient.getStockList("market_cap_basic", "desc", "fund"));

        if (profile == InvestmentProfile.MODERATE || profile == InvestmentProfile.AGGRESSIVE) {
            resultList.addAll(brapApiClient.getStockList("market_cap_basic", "desc", "bdr"));
        }

        return resultList.stream()
                .filter(s -> isEligibleByProfile(s, profile))
                .map(StockDTO::getStock)
                .collect(Collectors.toSet());
    }

    public Set<String> searchConservative() {
        return searchEligibleStocksByProfile(InvestmentProfile.CONSERVATIVE);
    }

    public Set<String> searchModerate() {
        return searchEligibleStocksByProfile(InvestmentProfile.MODERATE);
    }

    public Set<String> searchAggressive() {
        return searchEligibleStocksByProfile(InvestmentProfile.AGGRESSIVE);
    }

    public Optional<StockResponseDto> searchStockDetails(List<String> stockList) {
        return brapApiClient.getDetailsOfStocks(stockList);
    }

    private boolean isEligibleByProfile(StockDTO s, InvestmentProfile profile) {
        if (s.getMarketCap() == null || s.getSector() == null || s.getType() == null) {
            return false;
        }

        return switch (profile) {
            case CONSERVATIVE -> s.getMarketCap() > 5_000_000_000L &&
                    List.of("Finance", "Utilities", "Consumer Non-Durables", "Health Services", "Energy Minerals")
                            .contains(s.getSector());
            case MODERATE -> s.getMarketCap() > 2_000_000_000L &&
                    List.of("Industrial Services", "Technology Services", "Consumer Services", "Health Technology", "Non-Energy Minerals", "Retail Trade")
                            .contains(s.getSector());
            case AGGRESSIVE -> s.getMarketCap() > 1_000_000_000L &&
                    List.of("Electronic Technology", "Technology Services", "Communications", "Consumer Durables", "Retail Trade")
                            .contains(s.getSector());
            default -> false;
        };
    }
}