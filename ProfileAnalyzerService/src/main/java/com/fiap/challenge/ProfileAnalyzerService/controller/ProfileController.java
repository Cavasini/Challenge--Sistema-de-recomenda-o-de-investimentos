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

// Importe seus DTOs e o Serviço
// import com.seuprojeto.dto.ProfileAnalysisRequest;
// import com.seuprojeto.dto.ProfileData;
// import com.seuprojeto.service.ProfileAnalyzerService;

@RestController
@RequestMapping("/api/v1/profile") // Ou /api/v1/analysis, se preferir um nome mais específico
public class ProfileController {

    private final ProfileAnalyzerService profileAnalyzerService;

    // Injeção de dependências
    public ProfileController(ProfileAnalyzerService profileAnalyzerService) {
        this.profileAnalyzerService = profileAnalyzerService;
    }

    @PostMapping("/analyze") // Endpoint para a análise de perfil
    public ResponseEntity<ProfileData> analyzeProfile(@Valid @RequestBody ProfileAnalysisRequest request) {
        try {
            // 1. Validação do DTO de entrada pelo Jakarta Validation (@Valid)
            // 2. Validação customizada das respostas (chama o método do DTO)
            request.validateCustomAnswers();

            // 3. Invoca o serviço para analisar o perfil
            ProfileData profileData = profileAnalyzerService.analyzeAndClassifyProfile(request);

            // 4. Retorna o DTO ProfileData para o cliente
            return ResponseEntity.ok(profileData);

        } catch (IllegalArgumentException e) {
            // Erros de validação (ex: perguntas faltando, respostas inválidas)
            System.err.println("Erro de validação na requisição: " + e.getMessage());
            // Em um sistema real, você retornaria um DTO de erro mais estruturado
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            // Outros erros inesperados
            System.err.println("Erro interno no servidor: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }
}