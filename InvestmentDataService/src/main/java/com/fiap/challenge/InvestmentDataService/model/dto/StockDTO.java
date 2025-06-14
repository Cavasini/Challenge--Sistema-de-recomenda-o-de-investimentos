package com.fiap.challenge.InvestmentDataService.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects; // Importe esta classe

public class StockDTO {
    private String stock;
    private String name;
    private Double close;
    private Double change;
    private Long volume;

    @JsonProperty("market_cap")
    private Long marketCap;

    private String logo;
    private String sector;
    private String type;

    // Seus getters já estão aqui, então não preciso repeti-los.
    public String getStock() {
        return stock;
    }

    public String getName() {
        return name;
    }

    public Double getClose() {
        return close;
    }

    public Double getChange() {
        return change;
    }

    public Long getVolume() {
        return volume;
    }

    public Long getMarketCap() {
        return marketCap;
    }

    public String getLogo() {
        return logo;
    }

    public String getSector() {
        return sector;
    }

    public String getType() {
        return type;
    }

    // --- Métodos equals() e hashCode() para garantir unicidade no Set ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockDTO stockDTO = (StockDTO) o;

        return Objects.equals(stock, stockDTO.stock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stock);
    }
}