package com.fiap.challenge.RecommenderService.controller;

import com.fiap.challenge.RecommenderService.client.dto.FixedIncomeDTO;
import com.fiap.challenge.RecommenderService.client.dto.StockDetailResponse;
import com.fiap.challenge.RecommenderService.client.dto.VariableIncomeResponse;
import com.fiap.challenge.RecommenderService.model.*;
import com.fiap.challenge.RecommenderService.service.FixedIncomeAnalyzer;
import com.fiap.challenge.RecommenderService.service.PortfolioService;
import com.fiap.challenge.RecommenderService.service.VariableIncomeAnalyzer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/recommender")
public class RecommenderController {

    private final FixedIncomeAnalyzer fixedIncomeAnalyzer;
    private final VariableIncomeAnalyzer variableIncomeAnalyzer;
    private final PortfolioService portfolioService;

    public RecommenderController(FixedIncomeAnalyzer fixedIncomeAnalyzer, VariableIncomeAnalyzer variableIncomeAnalyzer, PortfolioService portfolioService){
        this.fixedIncomeAnalyzer = fixedIncomeAnalyzer;
        this.variableIncomeAnalyzer = variableIncomeAnalyzer;
        this.portfolioService = portfolioService;
    }

    @PostMapping()
    public ResponseEntity<?> processRecommendationRequest(@RequestBody ProfileData profileData) {
        System.out.println(profileData.getProfileClassification());
        System.out.println(profileData.getTotalScore());
        System.out.println(profileData.getUserId());
        InvestmentProfile profile;

        if (profileData.getProfileClassification().equals("Conservador")) {
            profile = InvestmentProfile.CONSERVATIVE;
        } else if (profileData.getProfileClassification().equals("Moderado")) {
            profile = InvestmentProfile.MODERATE;
        } else if (profileData.getProfileClassification().equals("Sofisticado")) {
            profile = InvestmentProfile.AGGRESSIVE;
        } else {
            return ResponseEntity.badRequest().body("O perfil de classificação informado é inválido.");
        }

        ResponseDTO response = new ResponseDTO(
                fixedIncomeAnalyzer.getFixedIncomesBasedOnProfile(profile),
                variableIncomeAnalyzer.getAndProcessGroupedVariableIncomes(profile)
        );


        try {
            UserRecomendation newPortfolio = new UserRecomendation();
            newPortfolio.setUserProfile(profileData);
            newPortfolio.setInvestmentRecommendations(response);
            newPortfolio.setCreatedAt(Instant.now());
            newPortfolio.setId(profileData.getUserId());
            portfolioService.savePortfolio(newPortfolio);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível salvar o portfólio no banco de dados. ERROR: " + e.getMessage(), e);
        }

        return ResponseEntity.ok(response);
    }
}
