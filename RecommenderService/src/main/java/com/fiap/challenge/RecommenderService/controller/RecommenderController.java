package com.fiap.challenge.RecommenderService.controller;

import com.fiap.challenge.RecommenderService.client.dto.FixedIncomeDTO;
import com.fiap.challenge.RecommenderService.client.dto.StockDetailResponse;
import com.fiap.challenge.RecommenderService.client.dto.VariableIncomeResponse;
import com.fiap.challenge.RecommenderService.model.InvestmentProfile;
import com.fiap.challenge.RecommenderService.model.ProfileData;
import com.fiap.challenge.RecommenderService.model.ResponseDTO;
import com.fiap.challenge.RecommenderService.model.StockScoreResult;
import com.fiap.challenge.RecommenderService.service.FixedIncomeAnalyzer;
import com.fiap.challenge.RecommenderService.service.VariableIncomeAnalyzer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/recommender")
public class RecommenderController {

    private final FixedIncomeAnalyzer fixedIncomeAnalyzer;
    private final VariableIncomeAnalyzer variableIncomeAnalyzer;

    public RecommenderController(FixedIncomeAnalyzer fixedIncomeAnalyzer, VariableIncomeAnalyzer variableIncomeAnalyzer){
        this.fixedIncomeAnalyzer = fixedIncomeAnalyzer;
        this.variableIncomeAnalyzer = variableIncomeAnalyzer;
    }

    @PostMapping()
    public ResponseEntity<ResponseDTO> processRecommendationRequest(@RequestBody ProfileData profileData) {
        if (profileData.getProfileClassification().equals("Conservador")) {
            ResponseDTO response = new ResponseDTO(
                    fixedIncomeAnalyzer.getFixedIncomesBasedOnProfile(InvestmentProfile.CONSERVATIVE),
                    variableIncomeAnalyzer.getAndProcessGroupedVariableIncomes(InvestmentProfile.CONSERVATIVE)
            );
            return ResponseEntity.ok(response);
        } else if (profileData.getProfileClassification().equals("Moderado")) {
            ResponseDTO response = new ResponseDTO(
                    fixedIncomeAnalyzer.getFixedIncomesBasedOnProfile(InvestmentProfile.MODERATE),
                    variableIncomeAnalyzer.getAndProcessGroupedVariableIncomes(InvestmentProfile.MODERATE)
            );
            return ResponseEntity.ok(response);
        } else if (profileData.getProfileClassification().equals("Sofisticado")) {
            ResponseDTO response = new ResponseDTO(
                    fixedIncomeAnalyzer.getFixedIncomesBasedOnProfile(InvestmentProfile.AGGRESSIVE),
                    variableIncomeAnalyzer.getAndProcessGroupedVariableIncomes(InvestmentProfile.AGGRESSIVE)
            );
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
