package com.fiap.challenge.RecommenderService.controller;

import com.fiap.challenge.RecommenderService.client.dto.FixedIncomeDTO;
import com.fiap.challenge.RecommenderService.client.dto.StockDetailResponse;
import com.fiap.challenge.RecommenderService.client.dto.VariableIncomeResponse;
import com.fiap.challenge.RecommenderService.model.InvestmentProfile;
import com.fiap.challenge.RecommenderService.model.StockScoreResult;
import com.fiap.challenge.RecommenderService.service.FixedIncomeAnalyzer;
import com.fiap.challenge.RecommenderService.service.VariableIncomeAnalyzer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class RecommernderController {

    private final FixedIncomeAnalyzer fixedIncomeAnalyzer;
    private final VariableIncomeAnalyzer variableIncomeAnalyzer;

    public RecommernderController(FixedIncomeAnalyzer fixedIncomeAnalyzer, VariableIncomeAnalyzer variableIncomeAnalyzer){
        this.fixedIncomeAnalyzer = fixedIncomeAnalyzer;
        this.variableIncomeAnalyzer = variableIncomeAnalyzer;
    }

    @GetMapping
    public List<FixedIncomeDTO> getFixedIncome(){
        return fixedIncomeAnalyzer.getFixedIncomesBasedOnProfile(InvestmentProfile.AGGRESSIVE);
    }

    @GetMapping("/variable")
    public List<StockScoreResult> getVariableIncome(){
        return variableIncomeAnalyzer.getAndProcessGroupedVariableIncomes();
    }
}
