package com.fiap.challenge.RecommenderService.service;

import com.fiap.challenge.RecommenderService.client.InvestmentDataServiceClient;
import com.fiap.challenge.RecommenderService.client.dto.StockDetailResponse;
import com.fiap.challenge.RecommenderService.client.dto.StockResult;
import com.fiap.challenge.RecommenderService.model.InvestmentProfile;
import com.fiap.challenge.RecommenderService.model.StockDTO;
import com.fiap.challenge.RecommenderService.model.StockScoreResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VariableIncomeAnalyzerTest {

    private InvestmentDataServiceClient client;
    private VariableIncomeAnalyzer analyzer;

    @BeforeEach
    void setup() {
        client = mock(InvestmentDataServiceClient.class);
        analyzer = new VariableIncomeAnalyzer(client);
    }

    @Test
    void testCalculateReturn() {
        double result = VariableIncomeAnalyzer.calculateReturn(110, 100);
        assertEquals(0.1, result, 0.0001);
    }

    @Test
    void testCalculateVolatility() {
        List<Double> prices = List.of(10.0, 10.5, 9.5, 10.2, 9.8);
        double result = VariableIncomeAnalyzer.calculateVolatility(prices);
        assertTrue(result > 0 && result < 1);
    }

    @Test
    void testCalculateMaxDrawdown() {
        List<Double> prices = List.of(100.0, 120.0, 80.0, 130.0, 90.0);
        double result = VariableIncomeAnalyzer.calculateMaxDrawdown(prices);
        assertEquals(0.3333, result, 0.01); // maior perda foi de 120 -> 80
    }

    @Test
    void testCalculatePE() {
        double pe = VariableIncomeAnalyzer.calculatePE(100, 5);
        assertEquals(20, pe);
    }

    @Test
    void testCalculatePB() {
        double pb = VariableIncomeAnalyzer.calculatePB(100, 1_000_000, 10_000_000);
        assertTrue(pb > 0);
    }

    @Test
    void testCalculateRSI() {
        List<Double> prices = List.of(10.0, 10.5, 10.2, 10.8, 11.0, 10.7, 11.2);
        double rsi = VariableIncomeAnalyzer.calculateRSI(prices, 5);
        assertTrue(rsi >= 0 && rsi <= 100);
    }

    @Test
    void testCalculateAverageVolume() {
        List<Long> vols = List.of(100L, 200L, 300L);
        assertEquals(200.0, VariableIncomeAnalyzer.calculateAverageVolume(vols));
    }

    @Test
    void testCalculatePositionInRange() {
        double pos = VariableIncomeAnalyzer.calculatePositionInRange(75, 50, 100);
        assertEquals(0.5, pos);
    }

    @Test
    void testScoreConservative() {
        StockDTO dto = mockStockDTO(100, 95, 10, 1_000_000, 1_000_000L);
        StockScoreResult result = VariableIncomeAnalyzer.calculateScoreConservative(dto);
        assertTrue(result.score >= 0 && result.score <= 10);
    }

    @Test
    void testScoreModerate() {
        StockDTO dto = mockStockDTO(100, 95, 5, 1_000_000, 1_000_000L);
        StockScoreResult result = VariableIncomeAnalyzer.calculateScoreModerate(dto);
        assertTrue(result.score >= 0 && result.score <= 10);
    }

    @Test
    void testScoreAggressive() {
        StockDTO dto = mockStockDTO(120, 100, 8, 1_000_000, 2_000_000L);
        StockScoreResult result = VariableIncomeAnalyzer.calculateScoreAggressive(dto);
        assertTrue(result.score >= 0 && result.score <= 10);
    }

    @Test
    void testAnalyzeWithoutDataReturnsEmptyList() {
        when(client.getVariableIncomesByProfile(InvestmentProfile.CONSERVATIVE))
                .thenReturn(Optional.empty());

        List<?> result = analyzer.analyze(InvestmentProfile.CONSERVATIVE);
        assertTrue(result.isEmpty());
    }

    private StockDTO mockStockDTO(double current, double previous, double eps, double equity, long volume) {
        List<Double> prices = List.of(100.0, 102.0, 101.0, 103.0, 104.0);
        List<Long> vols = List.of(volume, volume, volume);

        return new StockDTO(
                "TEST3", current, previous, eps, 10_000_000.0, equity,
                90.0, 120.0, volume, prices, vols,
                "Empresa Teste", "BRL", "http://logo.png", 1.0, 1.0
        );
    }
}


