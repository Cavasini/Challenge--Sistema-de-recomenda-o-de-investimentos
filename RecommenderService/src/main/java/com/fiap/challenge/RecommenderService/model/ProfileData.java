package com.fiap.challenge.RecommenderService.model;

import java.util.List;

public class ProfileData {
    private String userId; // Pode ser útil para o próximo serviço associar os dados ao usuário
    private Double totalScore;
    private String profileClassification; // Ex: "Conservador", "Moderado", "Sofisticado"
    private IdentifiedInterests identifiedInterests;

    // Construtor
    public ProfileData(String userId, Double totalScore, String profileClassification, IdentifiedInterests identifiedInterests) {
        this.userId = userId;
        this.totalScore = totalScore;
        this.profileClassification = profileClassification;
        this.identifiedInterests = identifiedInterests;
    }

    public String getUserId() { return userId; }
    public Double getTotalScore() { return totalScore; }
    public String getProfileClassification() { return profileClassification; }
    public IdentifiedInterests getIdentifiedInterests() { return identifiedInterests; }


    public static class IdentifiedInterests {
        private boolean liquidityNeeded;
        private String esgInterest; // "high", "medium", "low", "none"
        private List<String> macroeconomicConcerns; // Ex: ["inflation", "interestRates"]
        private String riskToleranceNotes; // Observações sobre a tolerância ao risco

        // Construtor
        public IdentifiedInterests(boolean liquidityNeeded, String esgInterest, List<String> macroeconomicConcerns, String riskToleranceNotes) {
            this.liquidityNeeded = liquidityNeeded;
            this.esgInterest = esgInterest;
            this.macroeconomicConcerns = macroeconomicConcerns;
            this.riskToleranceNotes = riskToleranceNotes;
        }

        // Getters
        public boolean isLiquidityNeeded() { return liquidityNeeded; }
        public String getEsgInterest() { return esgInterest; }
        public List<String> getMacroeconomicConcerns() { return macroeconomicConcerns; }
        public String getRiskToleranceNotes() { return riskToleranceNotes; }
    }
}
