package com.fiap.challenge.ProfileAnalyzerService.service;

import com.fiap.challenge.ProfileAnalyzerService.model.ProfileAnalysisRequest;
import com.fiap.challenge.ProfileAnalyzerService.model.ProfileData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// Importe os DTOs
// import com.seuprojeto.dto.ProfileAnalysisRequest; // DTO de Request
// import com.seuprojeto.dto.ProfileData; // DTO de Response
// import com.seuprojeto.dto.ProfileData.IdentifiedInterests; // Classe aninhada do ProfileData

@Service
public class ProfileAnalyzerService {

    /**
     * Processa as respostas do questionário do usuário, calcula o score,
     * classifica o perfil de investimento e identifica interesses específicos.
     *
     * @param request O DTO de requisição contendo as respostas do usuário.
     * @return Um objeto ProfileData que resume o perfil analisado do usuário.
     */
    public ProfileData analyzeAndClassifyProfile(ProfileAnalysisRequest request) {
        // A validação customizada (request.validateCustomAnswers()) deve ser chamada
        // na camada de Controller antes de invocar este serviço,
        // ou você pode chamá-la aqui se preferir.
        // Exemplo: request.validateCustomAnswers();

        // 1. Calcular o score total
        int totalScore = calculateScore(request.getAnswers());

        // 2. Classificar o perfil principal
        String profileClassification = classifyProfile(totalScore);

        // 3. Identificar interesses específicos (flags)
        ProfileData.IdentifiedInterests interests = identifySpecificInterests(request.getAnswers(), profileClassification);

        // 4. Retornar o ProfileData encapsulado para o próximo serviço
        return new ProfileData(
                request.getUserId(), // Passa o userId para o ProfileData
                totalScore,
                profileClassification,
                interests
        );
    }

    // --- Métodos Privados de Lógica (Mesmos da resposta anterior) ---

    private int calculateScore(Map<String, String> answers) {
        int score = 0;
        Map<String, Map<String, Integer>> scoreMap = new HashMap<>();

        scoreMap.put("q1", Map.of("a", 1, "b", 3, "c", 5, "d", 2, "e", 3));
        scoreMap.put("q2", Map.of("a", 1, "b", 3, "c", 5));
        scoreMap.put("q3", Map.of("a", 1, "b", 3, "c", 5, "d", 2));
        scoreMap.put("q4", Map.of("a", 0, "b", 1, "c", 2, "d", 3));
        scoreMap.put("q5", Map.of("a", 2, "b", 2, "c", 3, "d", 4));
        scoreMap.put("q6", Map.of("a", 1, "b", 3, "c", 5));
        scoreMap.put("q7", Map.of("a", 1, "b", 3, "c", 5));

        for (Map.Entry<String, String> entry : answers.entrySet()) {
            String question = entry.getKey();
            String answer = entry.getValue();
            if (scoreMap.containsKey(question) && scoreMap.get(question).containsKey(answer)) {
                score += scoreMap.get(question).get(answer);
            }
        }
        return score;
    }

    private String classifyProfile(int score) {
        if (score >= 6 && score <= 15) {
            return "Conservador";
        } else if (score >= 16 && score <= 25) {
            return "Moderado";
        } else if (score >= 26 && score <= 32) {
            return "Sofisticado";
        } else {
            return "Indefinido";
        }
    }

    private ProfileData.IdentifiedInterests identifySpecificInterests(Map<String, String> answers, String profileClassification) {
        boolean liquidityNeeded = answers.get("q2").equals("a");

        String esgInterest = "none";
        String q4Answer = answers.get("q4");
        if ("d".equals(q4Answer)) {
            esgInterest = "high";
        } else if ("c".equals(q4Answer)) {
            esgInterest = "medium";
        } else if ("b".equals(q4Answer)) {
            esgInterest = "low";
        }

        List<String> macroeconomicConcerns = new ArrayList<>();
        String q5Answer = answers.get("q5");
        if ("a".equals(q5Answer)) {
            macroeconomicConcerns.add("inflation");
        }
        if ("b".equals(q5Answer)) {
            macroeconomicConcerns.add("interestRates");
        }

        String riskToleranceNotes = "Tolerância a risco alinhada ao perfil classificado.";
        String q3Answer = answers.get("q3");
        if ("a".equals(q3Answer) && !"Conservador".equals(profileClassification)) {
            riskToleranceNotes = "Cauteloso com quedas acentuadas, apesar do perfil geral.";
        } else if ("b".equals(q3Answer)) {
            riskToleranceNotes = "Reage com preocupação, mas busca entender o cenário.";
        } else if ("d".equals(q3Answer)) {
            riskToleranceNotes = "Prioriza alinhamento com valores ESG mesmo em volatilidade.";
        }

        return new ProfileData.IdentifiedInterests(liquidityNeeded, esgInterest, macroeconomicConcerns, riskToleranceNotes);
    }
}