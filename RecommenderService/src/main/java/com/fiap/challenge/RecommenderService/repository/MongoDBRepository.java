package com.fiap.challenge.RecommenderService.repository;

import com.fiap.challenge.RecommenderService.model.UserRecomendation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoDBRepository extends MongoRepository<UserRecomendation, String> {

    Optional<UserRecomendation> findByUserProfileUserId(String userId);

    boolean existsById(String id);
}
