package com.fiap.challenge.RecommenderService.controller;

import com.fiap.challenge.RecommenderService.model.UserRecomendation;
import com.fiap.challenge.RecommenderService.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/portfolios")
public class PortifolioController {

    private final PortfolioService portfolioService;

    @Autowired
    public PortifolioController(PortfolioService portfolioService){
        this.portfolioService = portfolioService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserRecomendation> getUserPortfolio(@PathVariable String userId) {
        UserRecomendation portfolio = portfolioService.getPortfolioByUser(userId);
        return ResponseEntity.ok(portfolio);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity deleteUserPortfolio(@PathVariable String userId) {
        try {
            portfolioService.deletePortfolio(userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
