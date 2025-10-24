package com.fiap.challenge.ProfileAnalyzerService.service;

import com.fiap.challenge.ProfileAnalyzerService.model.ProfileAnalysisRequest;
import com.fiap.challenge.ProfileAnalyzerService.model.ProfileData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProfileAnalyzerServiceTest {

    private ProfileAnalyzerService profileAnalyzerService;

    @BeforeEach
    void setUp() {
        // Instancia o serviço diretamente, pois ele não tem dependências (é pura lógica)
        profileAnalyzerService = new ProfileAnalyzerService();
    }

    @Test
    @DisplayName("Deve classificar perfil como Conservador na pontuação mínima")
    void testAnalyzeAndClassifyProfile_Conservative() {
        // Arrange
        String userId = "user-001";
        Map<String, String> answers = Map.of(
                "q1", "a", // 1
                "q2", "a", // 1
                "q3", "a", // 1
                "q4", "a", // 0
                "q5", "a", // 2
                "q6", "a", // 1
                "q7", "a"  // 1
        );
        // Pontuação total = 6 (Mínimo possível)
        ProfileAnalysisRequest request = new ProfileAnalysisRequest(userId, answers, BigDecimal.ZERO);

        // Act
        ProfileData result = profileAnalyzerService.analyzeAndClassifyProfile(request);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(7, result.getTotalScore());
        assertEquals("Conservador", result.getProfileClassification());

        ProfileData.IdentifiedInterests interests = result.getIdentifiedInterests();
        assertNotNull(interests);
        assertTrue(interests.isLiquidityNeeded()); // q2 = a
        assertEquals("none", interests.getEsgInterest()); // q4 = a
        assertEquals(List.of("inflation"), interests.getMacroeconomicConcerns()); // q5 = a
        assertEquals("Tolerância a risco alinhada ao perfil classificado.", interests.getRiskToleranceNotes()); // q3=a e perfil=Conservador
    }

    @Test
    @DisplayName("Deve classificar perfil como Moderado")
    void testAnalyzeAndClassifyProfile_Moderate() {
        // Arrange
        String userId = "user-002";
        Map<String, String> answers = Map.of(
                "q1", "b", // 3
                "q2", "b", // 3
                "q3", "b", // 3
                "q4", "c", // 2
                "q5", "b", // 2
                "q6", "b", // 3
                "q7", "b"  // 3
        );
        // Pontuação total = 19
        ProfileAnalysisRequest request = new ProfileAnalysisRequest(userId, answers, BigDecimal.ZERO);

        // Act
        ProfileData result = profileAnalyzerService.analyzeAndClassifyProfile(request);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(19, result.getTotalScore());
        assertEquals("Moderado", result.getProfileClassification());

        ProfileData.IdentifiedInterests interests = result.getIdentifiedInterests();
        assertNotNull(interests);
        assertFalse(interests.isLiquidityNeeded()); // q2 = b
        assertEquals("medium", interests.getEsgInterest()); // q4 = c
        assertEquals(List.of("interestRates"), interests.getMacroeconomicConcerns()); // q5 = b
        assertEquals("Reage com preocupação, mas busca entender o cenário.", interests.getRiskToleranceNotes()); // q3 = b
    }

    @Test
    @DisplayName("Deve classificar perfil como Sofisticado na pontuação máxima")
    void testAnalyzeAndClassifyProfile_Sophisticated() {
        // Arrange
        String userId = "user-003";
        Map<String, String> answers = Map.of(
                "q1", "c", // 5
                "q2", "c", // 5
                "q3", "c", // 5
                "q4", "d", // 3
                "q5", "d", // 4
                "q6", "c", // 5
                "q7", "c"  // 5
        );
        // Pontuação total = 32 (Máximo possível)
        ProfileAnalysisRequest request = new ProfileAnalysisRequest(userId, answers, BigDecimal.ZERO);

        // Act
        ProfileData result = profileAnalyzerService.analyzeAndClassifyProfile(request);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(32, result.getTotalScore());
        assertEquals("Sofisticado", result.getProfileClassification());

        ProfileData.IdentifiedInterests interests = result.getIdentifiedInterests();
        assertNotNull(interests);
        assertFalse(interests.isLiquidityNeeded()); // q2 = c
        assertEquals("high", interests.getEsgInterest()); // q4 = d
        assertEquals(Collections.emptyList(), interests.getMacroeconomicConcerns()); // q5 = d
        assertEquals("Tolerância a risco alinhada ao perfil classificado.", interests.getRiskToleranceNotes()); // q3 = c
    }

    @Test
    @DisplayName("Deve identificar nota de risco desalinhada (Perfil Moderado mas cauteloso)")
    void testAnalyzeAndClassifyProfile_RiskNoteMismatch() {
        // Arrange
        String userId = "user-004";
        Map<String, String> answers = Map.of(
                "q1", "b", // 3
                "q2", "b", // 3
                "q3", "a", // 1 (Cauteloso)
                "q4", "c", // 2
                "q5", "b", // 2
                "q6", "b", // 3
                "q7", "b"  // 3
        );
        // Pontuação total = 17
        ProfileAnalysisRequest request = new ProfileAnalysisRequest(userId, answers, BigDecimal.ZERO);

        // Act
        ProfileData result = profileAnalyzerService.analyzeAndClassifyProfile(request);

        // Assert
        assertNotNull(result);
        assertEquals(17, result.getTotalScore());
        assertEquals("Moderado", result.getProfileClassification()); // Perfil é Moderado

        ProfileData.IdentifiedInterests interests = result.getIdentifiedInterests();
        assertNotNull(interests);
        // A nota de risco deve refletir a resposta 'a' da q3, mesmo o perfil sendo Moderado
        assertEquals("Cauteloso com quedas acentuadas, apesar do perfil geral.", interests.getRiskToleranceNotes());
    }
}
