package com.fiap.challenge.RecommenderService.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper; // Importar ObjectMapper
import com.fasterxml.jackson.databind.type.CollectionType; // Importar CollectionType

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects; // Importar Objects se estiver usando equals/hashCode/toString

public record StockResult(
        String currency,
        Double marketCap,
        String shortName,
        String longName,
        Double regularMarketChange,
        Double regularMarketChangePercent,
        OffsetDateTime regularMarketTime,
        Double regularMarketPrice,
        Double regularMarketDayHigh,
        String regularMarketDayRange,
        Double regularMarketDayLow,
        Long regularMarketVolume,
        Double regularMarketPreviousClose,
        Double regularMarketOpen,
        String fiftyTwoWeekRange,
        Double fiftyTwoWeekLow,
        Double fiftyTwoWeekHigh,
        String symbol,
        String logourl,
        String usedInterval,
        String usedRange,
        List<HistoricalDataPrice> historicalDataPrice,
        List<String> validRanges,
        List<String> validIntervals,

        Object balanceSheetHistory,
        Double priceEarnings,
        Double earningsPerShare
) {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @JsonIgnore
    public List<BalanceSheetStatement> getParsedBalanceSheetHistory() {
        List<?> rawStatements = null;

        if (balanceSheetHistory instanceof List<?>) {
            rawStatements = (List<?>) balanceSheetHistory;
        } else if (balanceSheetHistory instanceof Map<?, ?>) {
            Map<String, ?> map = (Map<String, ?>) balanceSheetHistory;
            if (map.containsKey("balanceSheetStatements")) {
                rawStatements = (List<?>) map.get("balanceSheetStatements");
            }
        }

        if (rawStatements == null || rawStatements.isEmpty()) {
            return List.of();
        }

        // Agora, itere sobre os LinkedHashMaps e mapeie-os para BalanceSheetStatement
        List<BalanceSheetStatement> parsedStatements = new ArrayList<>();
        for (Object item : rawStatements) {
            if (item instanceof Map<?, ?>) {
                try {
                    // Mapeia o LinkedHashMap para a classe BalanceSheetStatement
                    BalanceSheetStatement statement = objectMapper.convertValue(item, BalanceSheetStatement.class);
                    parsedStatements.add(statement);
                } catch (IllegalArgumentException e) {
                    System.err.println("Erro ao mapear BalanceSheetStatement: " + e.getMessage());
                }
            } else if (item instanceof BalanceSheetStatement) {
                // Caso raro onde já foi desserializado (se o Jackson fizer isso automaticamente)
                parsedStatements.add((BalanceSheetStatement) item);
            }
        }
        return parsedStatements;
    }

    public Long getFirstTotalStockholderEquity() {
        List<BalanceSheetStatement> statements = getParsedBalanceSheetHistory();
        if (!statements.isEmpty()) { // <--- Ele só entra aqui se a lista NÃO estiver vazia
            return statements.get(0).totalStockholderEquity();
        }
        return null; // <--- Ele retorna null se a lista estiver vazia
    }

    public List<Double> getPriceHistory() {
        if (this.historicalDataPrice == null || this.historicalDataPrice.isEmpty()) {
            return new ArrayList<>();
        }

        List<Double> priceList = new ArrayList<>();
        for (HistoricalDataPrice data : this.historicalDataPrice) {
            if (data != null && data.close() != null) { // Era 'data.volume()' aqui, mudei para 'data.close()'
                priceList.add(data.close()); // Não precisa de parse se o tipo já for Double
            }
        }
        return priceList;
    }

    public List<Long> getVolumeHistory() {
        if (this.historicalDataPrice == null || this.historicalDataPrice.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> volumeList = new ArrayList<>();
        for (HistoricalDataPrice data : this.historicalDataPrice) {
            if (data != null && data.volume() != null) {
                volumeList.add(data.volume());
            }
        }
        return volumeList;
    }
}