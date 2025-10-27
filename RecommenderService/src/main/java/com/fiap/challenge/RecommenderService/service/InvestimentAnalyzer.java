package com.fiap.challenge.RecommenderService.service;

import com.fiap.challenge.RecommenderService.model.InvestimentRecommendation;
import com.fiap.challenge.RecommenderService.model.InvestmentProfile;

import java.util.List;

public interface InvestimentAnalyzer<T extends InvestimentRecommendation> {

    List<T> analyze(InvestmentProfile profile);
}
