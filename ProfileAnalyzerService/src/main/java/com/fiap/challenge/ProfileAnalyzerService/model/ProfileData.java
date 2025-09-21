package com.fiap.challenge.ProfileAnalyzerService.model;

import java.util.List;

public class ProfileData {
    private String userId; // Pode ser útil para o próximo serviço associar os dados ao usuário
    private int totalScore;
    private String profileClassification; // Ex: "Conservador", "Moderado", "Sofisticado"
    private IdentifiedInterests identifiedInterests;

    public ProfileData(String userId, int totalScore, String profileClassification, IdentifiedInterests identifiedInterests) {
        this.userId = userId;
        this.totalScore = totalScore;
        this.profileClassification = profileClassification;
        this.identifiedInterests = identifiedInterests;
    }

    public String getUserId() { return userId; }
    public int getTotalScore() { return totalScore; }
    public String getProfileClassification() { return profileClassification; }
    public IdentifiedInterests getIdentifiedInterests() { return identifiedInterests; }


    public static class IdentifiedInterests {
        private boolean liquidityNeeded;
        private String esgInterest; // "high", "medium", "low", "none"
        private List<String> macroeconomicConcerns; // Ex: ["inflation", "interestRates"]
        private String riskToleranceNotes; // Observações sobre a tolerância ao risco

        public IdentifiedInterests(boolean liquidityNeeded, String esgInterest, List<String> macroeconomicConcerns, String riskToleranceNotes) {
            this.liquidityNeeded = liquidityNeeded;
            this.esgInterest = esgInterest;
            this.macroeconomicConcerns = macroeconomicConcerns;
            this.riskToleranceNotes = riskToleranceNotes;
        }

        public boolean isLiquidityNeeded() { return liquidityNeeded; }
        public String getEsgInterest() { return esgInterest; }
        public List<String> getMacroeconomicConcerns() { return macroeconomicConcerns; }
        public String getRiskToleranceNotes() { return riskToleranceNotes; }
    }
}