package com.fiap.challenge.ProfileAnalyzerService.controller;

import com.fiap.challenge.ProfileAnalyzerService.model.ProfileAnalysisRequest;
import com.fiap.challenge.ProfileAnalyzerService.model.ProfileData;
import com.fiap.challenge.ProfileAnalyzerService.service.ProfileAnalyzerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileAnalyzerService profileAnalyzerService;

    public ProfileController(ProfileAnalyzerService profileAnalyzerService) {
        this.profileAnalyzerService = profileAnalyzerService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<ProfileData> analyzeProfile(@Valid @RequestBody ProfileAnalysisRequest request) {
        try {
            request.validateCustomAnswers();

            ProfileData profileData = profileAnalyzerService.analyzeAndClassifyProfile(request);

            return ResponseEntity.ok(profileData);

        } catch (IllegalArgumentException e) {
            System.err.println("Erro de validação na requisição: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            System.err.println("Erro interno no servidor: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }
}