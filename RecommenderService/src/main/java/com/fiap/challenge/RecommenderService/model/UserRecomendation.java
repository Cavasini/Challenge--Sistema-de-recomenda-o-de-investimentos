package com.fiap.challenge.RecommenderService.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Data
@Document(collection = "user_recomendations")
public class UserRecomendation {

    @Id
    private String id; // O _id do documento no MongoDB
    private ProfileData userProfile;
    private Instant createdAt; // Data de criação do registro

    private ResponseDTO investmentRecommendations;
}
