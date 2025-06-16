package com.fiap.challenge.RecommenderService.model;

import java.time.LocalDate;

public class Dividendo {
    private LocalDate paymentDate;
    private double rate;

    public Dividendo(String paymentDateStr, double rate) {
        try {
            this.paymentDate = paymentDateStr != null ? LocalDate.parse(paymentDateStr.substring(0, 10)) : null;
        } catch (Exception e) {
            this.paymentDate = null;
        }
        this.rate = rate;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public double getRate() {
        return rate;
    }
}
