package com.fiap.challenge.ProfileAnalyzerService.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ProfileAnalysisRequest {

    private String userId;

    @NotNull(message = "As respostas do questionário são obrigatórias.")
    @Size(min = 7, max = 7, message = "Devem ser fornecidas respostas para todas as 7 perguntas (q1 a q7).")
    private Map<String, String> answers;

    @DecimalMin(value = "0.0", inclusive = true, message = "O valor do investimento inicial deve ser não negativo.")
    private BigDecimal monthlyInvestmentValue; // Mantido, mesmo que não seja usado diretamente neste serviço

    public ProfileAnalysisRequest() {
    }

    public ProfileAnalysisRequest(String userId, Map<String, String> answers, BigDecimal monthlyInvestmentValue) {
        this.userId = userId;
        this.answers = answers;
        this.monthlyInvestmentValue = monthlyInvestmentValue;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Map<String, String> getAnswers() { return answers; }
    public void setAnswers(Map<String, String> answers) { this.answers = answers; }
    public BigDecimal getInitialInvestmentValue() { return monthlyInvestmentValue; }
    public void setInitialInvestmentValue(BigDecimal initialInvestmentValue) { this.monthlyInvestmentValue = monthlyInvestmentValue; }

    public void validateCustomAnswers() {
        if (answers == null) {
            throw new IllegalArgumentException("As respostas não podem ser nulas.");
        }

        Set<String> expectedQuestions = new HashSet<>();
        expectedQuestions.add("q1"); expectedQuestions.add("q2"); expectedQuestions.add("q3");
        expectedQuestions.add("q4"); expectedQuestions.add("q5"); expectedQuestions.add("q6"); expectedQuestions.add("q7");

        if (!answers.keySet().containsAll(expectedQuestions)) {
            Set<String> missingQuestions = new HashSet<>(expectedQuestions);
            missingQuestions.removeAll(answers.keySet());
            throw new IllegalArgumentException("Faltam respostas para as perguntas: " + String.join(", ", missingQuestions));
        }

        Map<String, Set<String>> validOptions = Map.of(
                "q1", Set.of("a", "b", "c", "d", "e"),
                "q2", Set.of("a", "b", "c"),
                "q3", Set.of("a", "b", "c", "d"),
                "q4", Set.of("a", "b", "c", "d"),
                "q5", Set.of("a", "b", "c", "d"),
                "q6", Set.of("a", "b", "c"),
                "q7", Set.of("a", "b", "c")
        );

        for (Map.Entry<String, String> entry : answers.entrySet()) {
            String question = entry.getKey();
            String answer = entry.getValue();

            if (validOptions.containsKey(question) && !validOptions.get(question).contains(answer)) {
                throw new IllegalArgumentException(
                        String.format("Resposta inválida '%s' para a pergunta '%s'. Opções válidas: %s",
                                answer, question, String.join(", ", validOptions.get(question)))
                );
            }
        }
    }
}