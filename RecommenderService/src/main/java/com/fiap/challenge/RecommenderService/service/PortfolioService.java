package com.fiap.challenge.RecommenderService.service;

import com.fiap.challenge.RecommenderService.model.UserRecomendation;
import com.fiap.challenge.RecommenderService.repository.MongoDBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PortfolioService {

    private final MongoDBRepository repository;

    @Autowired
    public PortfolioService(MongoDBRepository repository) {
        this.repository = repository;
    }

    public UserRecomendation savePortfolio(UserRecomendation portfolio) {
        System.out.println("Salvando recomendação para o usuário: " + portfolio.getUserProfile().getUserId());
        return repository.save(portfolio);
    }

    public UserRecomendation getPortfolioByUser(String userId) {
        return repository.findByUserProfileUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Portfólio não encontrado para o usuário com ID: " + userId
                ));
    }
}
