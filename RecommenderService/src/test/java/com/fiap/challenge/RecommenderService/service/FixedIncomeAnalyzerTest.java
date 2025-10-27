package com.fiap.challenge.RecommenderService.service;

import com.fiap.challenge.RecommenderService.client.InvestmentDataServiceClient;
import com.fiap.challenge.RecommenderService.client.dto.FixedIncomeDTO;
import com.fiap.challenge.RecommenderService.client.dto.InvestmentDataResponse;
import com.fiap.challenge.RecommenderService.model.InvestmentProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class FixedIncomeAnalyzerTest {

    private InvestmentDataServiceClient mockClient;
    private FixedIncomeAnalyzer analyzer;

    @BeforeEach
    void setup() {
        mockClient = Mockito.mock(InvestmentDataServiceClient.class);
        analyzer = new FixedIncomeAnalyzer(mockClient);
    }

    // 🔹 Helper para criar rapidamente DTOs
    private FixedIncomeDTO dto(
            String name, String indexer, double indexerRate, Integer riskScore,
            boolean liquidity, int yearsToMaturity, double minAmount
    ) {
        LocalDate maturity = yearsToMaturity > 0 ? LocalDate.now().plusYears(yearsToMaturity) : null;
        return new FixedIncomeDTO(
                name,
                "CDB",
                indexerRate,
                indexer,
                false, // isTaxExempt
                liquidity,
                maturity != null ? maturity : null, // agora String
                minAmount,
                "Banco XP",
                riskScore,
                "API"
        );
    }

    @Test
    void shouldFilterAndSortFixedIncomesForConservativeProfile() {
        // given
        List<FixedIncomeDTO> allIncomes = List.of(
                dto("Tesouro Selic", "Selic", 0.1, 1, true, 2, 1000),
                dto("CDB IPCA", "IPCA", 6.0, 2, false, 4, 5000), // risco e prazo altos
                dto("CDB CDI", "CDI", 0.12, 1, true, 3, 10000)
        );

        InvestmentDataResponse response = new InvestmentDataResponse(
                allIncomes, "4.5", "10.75"
        );
        when(mockClient.getFixedIncome()).thenReturn(Optional.of(response));

        // when
        List<FixedIncomeDTO> result = analyzer.analyze(InvestmentProfile.CONSERVATIVE);

        // then
        assertEquals(2, result.size(), "Deve retornar apenas 2 investimentos válidos");
        assertTrue(result.stream().allMatch(i -> i.issuerRiskScore() <= 1));
        assertTrue(result.stream().allMatch(i -> List.of("Selic", "CDI").contains(i.indexer())));
    }

    @Test
    void shouldAllowMoreRiskAndLongerMaturitiesForAggressiveProfile() {
        // given
        List<FixedIncomeDTO> allIncomes = List.of(
                dto("Tesouro IPCA 2040", "IPCA", 6.0, 3, false, 15, 1000),
                dto("Prefixado 2030", "Prefixado", 12.0, 3, false, 8, 5000)
        );

        InvestmentDataResponse response = new InvestmentDataResponse(
                allIncomes, "4.5", "10.75"
        );
        when(mockClient.getFixedIncome()).thenReturn(Optional.of(response));

        // when
        List<FixedIncomeDTO> result = analyzer.analyze(InvestmentProfile.AGGRESSIVE);

        // then
        assertEquals(1, result.size());
        assertEquals("Prefixado 2030", result.get(0).name(), "Deve estar ordenado por maior yield calculado");
    }

    @Test
    void shouldFilterByLiquidityForConservativeProfile() {
        // given
        List<FixedIncomeDTO> allIncomes = List.of(
                dto("CDB Liquidez Diária", "CDI", 0.1, 1, true, 2, 1000),
                dto("CDB Sem Liquidez", "CDI", 0.2, 1, false, 2, 1000)
        );

        InvestmentDataResponse response = new InvestmentDataResponse(allIncomes, "4.5", "10.75");
        when(mockClient.getFixedIncome()).thenReturn(Optional.of(response));

        // when
        List<FixedIncomeDTO> result = analyzer.analyze(InvestmentProfile.CONSERVATIVE);

        // then
        assertEquals(1, result.size());
        assertTrue(result.get(0).dailyLiquidity(), "Conservador só aceita liquidez diária");
    }

    @Test
    void shouldLimitResultsByProfile() {
        // given
        List<FixedIncomeDTO> incomes = List.of(
                dto("CDB1", "CDI", 10, 2, true, 2, 1000),
                dto("CDB2", "CDI", 9, 2, true, 2, 1000),
                dto("CDB3", "CDI", 8, 2, true, 2, 1000),
                dto("CDB4", "CDI", 7, 2, true, 2, 1000),
                dto("CDB5", "CDI", 6, 2, true, 2, 1000)
        );

        InvestmentDataResponse response = new InvestmentDataResponse(incomes, "4.5", "10.75");
        when(mockClient.getFixedIncome()).thenReturn(Optional.of(response));

        // when
        List<FixedIncomeDTO> result = analyzer.analyze(InvestmentProfile.MODERATE);

        // then
        assertEquals(4, result.size(), "Perfil moderado deve retornar no máximo 4 ativos");
    }

    @Test
    void shouldHandleNullMaturityDate() {
        // given
        List<FixedIncomeDTO> incomes = List.of(
                dto("ETF Sem Vencimento", "CDI", 10, 1, true, 0, 1000)
        );

        InvestmentDataResponse response = new InvestmentDataResponse(incomes, "4.5", "10.75");
        when(mockClient.getFixedIncome()).thenReturn(Optional.of(response));

        // when
        List<FixedIncomeDTO> result = analyzer.analyze(InvestmentProfile.CONSERVATIVE);

        // then
        assertEquals(1, result.size(), "Deve aceitar produtos sem vencimento");
    }
}

