package com.fiap.challenge.ProfileAnalyzerService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.challenge.ProfileAnalyzerService.model.ProfileAnalysisRequest;
import com.fiap.challenge.ProfileAnalyzerService.model.ProfileData;
import com.fiap.challenge.ProfileAnalyzerService.service.ProfileAnalyzerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Imports estáticos para os matchers do jsonPath
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileAnalyzerService mockProfileAnalyzerService;

    private ProfileAnalysisRequest baseRequest;
    private Map<String, String> baseAnswers;

    @BeforeEach
    void setUp() {
        // Cria um mapa de respostas válidas genérico
        baseAnswers = new HashMap<>();
        baseAnswers.put("q1", "a");
        baseAnswers.put("q2", "b");
        baseAnswers.put("q3", "c");
        baseAnswers.put("q4", "d");
        baseAnswers.put("q5", "a");
        baseAnswers.put("q6", "b");
        baseAnswers.put("q7", "c");

        // Cria uma requisição base válida
        baseRequest = new ProfileAnalysisRequest(
                "user-test-123",
                baseAnswers,
                new BigDecimal("500.00")
        );
    }

    // =================================================================
    // TESTE DE SUCESSO 1: Cenário "Conservador" (fornecido por você)
    // =================================================================

    @Test
    void testAnalyzeProfile_Success_ConservativeProfile() throws Exception {
        // --- Arrange (Organizar) ---

        // 1. Criar o mapa de respostas exato da "maria_iniciante"
        Map<String, String> mariaAnswers = new HashMap<>();
        mariaAnswers.put("q1", "a");
        mariaAnswers.put("q2", "c");
        mariaAnswers.put("q3", "a");
        mariaAnswers.put("q4", "a");
        mariaAnswers.put("q5", "d");
        mariaAnswers.put("q6", "a");
        mariaAnswers.put("q7", "a");

        // 2. Criar o objeto de Request
        ProfileAnalysisRequest mariaRequest = new ProfileAnalysisRequest(
                "maria_iniciante",
                mariaAnswers,
                new BigDecimal("1000.00")
        );

        // 3. Criar a Resposta (ProfileData) que simularemos (baseado no seu exemplo)
        ProfileData.IdentifiedInterests conservativeInterests = new ProfileData.IdentifiedInterests(
                false, // liquidityNeeded
                "none", // esgInterest
                Collections.emptyList(), // macroeconomicConcerns
                "Tolerância a risco alinhada ao perfil classificado." // riskToleranceNotes
        );

        ProfileData mockResponseData = new ProfileData(
                "maria_iniciante", // userId
                13, // totalScore
                "Conservador", // profileClassification
                conservativeInterests
        );

        // 4. Instrui o mock do serviço
        when(mockProfileAnalyzerService.analyzeAndClassifyProfile(any(ProfileAnalysisRequest.class)))
                .thenReturn(mockResponseData);

        // --- Act (Agir) & Assert (Verificar) ---
        mockMvc.perform(post("/api/v1/profile/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mariaRequest)))
                .andExpect(status().isOk())
                // 7. Verifica os campos do JSON (usando "userId" como está na classe ProfileData)
                .andExpect(jsonPath("$.userId", is("maria_iniciante")))
                .andExpect(jsonPath("$.totalScore", is(13)))
                .andExpect(jsonPath("$.profileClassification", is("Conservador")))
                .andExpect(jsonPath("$.identifiedInterests.liquidityNeeded", is(false)))
                .andExpect(jsonPath("$.identifiedInterests.esgInterest", is("none")))
                .andExpect(jsonPath("$.identifiedInterests.macroeconomicConcerns", empty())) // Verifica lista vazia
                .andExpect(jsonPath("$.identifiedInterests.riskToleranceNotes", is("Tolerância a risco alinhada ao perfil classificado.")));

        // Verifica se o serviço foi chamado
        verify(mockProfileAnalyzerService).analyzeAndClassifyProfile(any(ProfileAnalysisRequest.class));
    }

    // =================================================================
    // TESTE DE SUCESSO 2: Cenário "Agressivo" (Exemplo)
    // =================================================================

    @Test
    void testAnalyzeProfile_Success_AggressiveProfile() throws Exception {
        // --- Arrange ---
        // 1. Respostas de um perfil agressivo (ex: q1='e', q3='d')
        Map<String, String> aggressiveAnswers = new HashMap<>();
        aggressiveAnswers.put("q1", "e"); // +10 anos
        aggressiveAnswers.put("q2", "a"); // Experiente
        aggressiveAnswers.put("q3", "d"); // Aumentaria posição
        aggressiveAnswers.put("q4", "d"); // Aceita alta volatilidade
        aggressiveAnswers.put("q5", "a"); // Foco em crescimento
        aggressiveAnswers.put("q6", "c"); // Prioriza ESG
        aggressiveAnswers.put("q7", "c"); // Preocupado com câmbio

        ProfileAnalysisRequest aggressiveRequest = new ProfileAnalysisRequest(
                "user-aggressive-456",
                aggressiveAnswers,
                new BigDecimal("10000.00")
        );

        // 2. Mock da Resposta
        ProfileData.IdentifiedInterests aggressiveInterests = new ProfileData.IdentifiedInterests(
                false,
                "high",
                Arrays.asList("exchangeRate"),
                "Alta tolerância ao risco."
        );
        ProfileData mockResponseData = new ProfileData(
                "user-aggressive-456", 45, "Agressivo", aggressiveInterests
        );

        // 3. Mock do Serviço
        when(mockProfileAnalyzerService.analyzeAndClassifyProfile(any(ProfileAnalysisRequest.class)))
                .thenReturn(mockResponseData);

        // --- Act & Assert ---
        mockMvc.perform(post("/api/v1/profile/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aggressiveRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is("user-aggressive-456")))
                .andExpect(jsonPath("$.totalScore", is(45)))
                .andExpect(jsonPath("$.profileClassification", is("Agressivo")))
                .andExpect(jsonPath("$.identifiedInterests.esgInterest", is("high")));
    }


    // =================================================================
    // TESTES DE VALIDAÇÃO E ERRO
    // =================================================================

    @Test
    void testAnalyzeProfile_BeanValidationFails_NullAnswers() throws Exception {
        // Teste para @NotNull
        baseRequest.setAnswers(null);

        mockMvc.perform(post("/api/v1/profile/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAnalyzeProfile_BeanValidationFails_WrongSize() throws Exception {
        // Teste para @Size(min = 7)
        baseAnswers.remove("q7");
        baseRequest.setAnswers(baseAnswers);

        mockMvc.perform(post("/api/v1/profile/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAnalyzeProfile_CustomValidationFails_MissingQuestion() throws Exception {
        // Teste para a validação customizada (falta q7)
        baseAnswers.remove("q7");
        baseAnswers.put("q8", "a"); // Adiciona uma chave errada para manter o size = 7
        baseRequest.setAnswers(baseAnswers);

        mockMvc.perform(post("/api/v1/profile/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseRequest)))
                .andExpect(status().isBadRequest()); // Tratado pelo catch (IllegalArgumentException)
    }

    @Test
    void testAnalyzeProfile_CustomValidationFails_InvalidAnswerOption() throws Exception {
        // Teste para a validação customizada (opção "z" inválida)
        baseAnswers.put("q1", "z");
        baseRequest.setAnswers(baseAnswers);

        mockMvc.perform(post("/api/v1/profile/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseRequest)))
                .andExpect(status().isBadRequest()); // Tratado pelo catch (IllegalArgumentException)
    }

    @Test
    void testAnalyzeProfile_InternalServerError() throws Exception {
        // Teste para o catch genérico (Exception)
        when(mockProfileAnalyzerService.analyzeAndClassifyProfile(any(ProfileAnalysisRequest.class)))
                .thenThrow(new RuntimeException("Simulação de erro interno no serviço"));

        mockMvc.perform(post("/api/v1/profile/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseRequest)))
                .andExpect(status().isInternalServerError()); // Espera HTTP 500
    }
}
