package com.fiap.challenge.RecommenderService.model;

public class StockScoreResult implements InvestimentRecommendation{
    public String ticket;
    public String longName;
    public String currency;
    public String logoUrl;
    public Double regularMarketPrice;
    public Double regularMarketChange;
    public Double regularMarketChancePercent;
    public double score;


    public StockScoreResult(String ticket, String longName, String currency, String logoUrl, Double regularMarketPrice, Double regularMarketChange, Double regularMarketChancePercent, double score) {
        this.ticket = ticket;
        this.longName = longName;
        this.currency = currency;
        this.logoUrl = logoUrl;
        this.regularMarketPrice = regularMarketPrice;
        this.regularMarketChange = regularMarketChange;
        this.regularMarketChancePercent = regularMarketChancePercent;
        this.score = score;
    }
}
