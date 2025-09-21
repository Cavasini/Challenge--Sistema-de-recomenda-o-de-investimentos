package com.fiap.challenge.RecommenderService.controller;

import com.fiap.challenge.RecommenderService.model.UserRecomendation;
import com.fiap.challenge.RecommenderService.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/portfolios")
public class PortifolioController {

    private final PortfolioService portfolioService;

    @Autowired
    public PortifolioController(PortfolioService portfolioService){
        this.portfolioService = portfolioService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserRecomendation> getPortfolioByUserId(@PathVariable String userId) {
        UserRecomendation portfolio = portfolioService.getPortfolioByUser(userId);
        return ResponseEntity.ok(portfolio);
    }
}
