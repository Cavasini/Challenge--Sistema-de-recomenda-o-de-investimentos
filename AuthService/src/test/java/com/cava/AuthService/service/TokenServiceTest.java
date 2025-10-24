package com.cava.AuthService.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cava.AuthService.model.User;
// Assumindo que sua enum de Role está neste pacote
import com.cava.AuthService.model.UserRole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID; // Importa a classe UUID

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private TokenService tokenService;

    @Mock
    private User mockUser;

    // Chave secreta usada APENAS para testes.
    private final String testSecret = "my-test-secret-key-that-is-long-enough-for-hmac256";
    private Algorithm testAlgorithm;
    private Algorithm wrongAlgorithm; // Para testar assinaturas inválidas

    @BeforeEach
    void setUp() {
        // Instanciamos o serviço manualmente, passando a chave de teste.
        tokenService = new TokenService(testSecret);

        // Algoritmos para criar tokens válidos e inválidos durante os testes
        testAlgorithm = Algorithm.HMAC256(testSecret.getBytes(StandardCharsets.UTF_8));
        wrongAlgorithm = Algorithm.HMAC256("another-wrong-secret-key".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Deve gerar um token JWT válido com as claims corretas")
    void testGenerateToken_Success() {
        // 1. Arrange
        // Configura o mockUser para retornar os dados esperados
        UUID userId = UUID.randomUUID();

        when(mockUser.getId()).thenReturn(userId); // Passando o objeto UUID
        // MUDANÇA: Adicionando o setup do mock para a role
        when(mockUser.getRole()).thenReturn(UserRole.USER);

        // 2. Act
        String token = tokenService.generateToken(mockUser);

        // 3. Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Decodifica o token para verificar seu conteúdo (claims)
        DecodedJWT decodedJWT = JWT.require(testAlgorithm).build().verify(token);

        assertEquals("auth-api", decodedJWT.getIssuer(), "Issuer está incorreto");
        assertEquals(userId.toString(), decodedJWT.getSubject(), "Subject (ID do usuário) está incorreto");

        // MUDANÇA: Verificamos se a claim 'role' existe e tem o valor "USER"
        assertEquals("USER", decodedJWT.getClaim("role").asString(), "Claim 'role' está incorreta");

        // Verifica se a expiração está no futuro
        assertTrue(decodedJWT.getExpiresAtAsInstant().isAfter(Instant.now()), "Token já foi gerado expirado");
    }

    @Test
    @DisplayName("Deve lançar NullPointerException se o ID do usuário for nulo")
    void testGenerateToken_NullId_ThrowsNPE() {
        // 1. Arrange
        // O ID nulo causará um NullPointerException em user.getId().toString()
        when(mockUser.getId()).thenReturn(null);
        // Não precisamos mockar getRole() aqui, pois o getId() é chamado antes e falhará.

        // 2. Act & 3. Assert
        // O try-catch no seu método 'generateToken' só captura JWTCreationException,
        // não NullPointerException. Este teste expõe essa fragilidade.
        assertThrows(NullPointerException.class, () -> {
            tokenService.generateToken(mockUser);
        }, "Deveria lançar NullPointerException se o ID for nulo");
    }

    @Test
    @DisplayName("Deve lançar NullPointerException se a Role do usuário for nula")
    void testGenerateToken_NullRole_ThrowsNPE() {
        // 1. Arrange
        // O ID está OK
        when(mockUser.getId()).thenReturn(UUID.randomUUID());
        // Mas a Role é nula
        when(mockUser.getRole()).thenReturn(null);
        // Isso causará NPE em user.getRole().name()

        // 2. Act & 3. Assert
        assertThrows(NullPointerException.class, () -> {
            tokenService.generateToken(mockUser);
        }, "Deveria lançar NullPointerException se a Role for nula");
    }

    @Test
    @DisplayName("Deve validar um token JWT válido e retornar o Subject (ID)")
    void testValidateToken_Success() {
        // 1. Arrange
        // Gera um token válido primeiro
        UUID userId = UUID.randomUUID();
        when(mockUser.getId()).thenReturn(userId);
        // MUDANÇA: Adicionando o setup do mock para a role, para evitar NPE
        when(mockUser.getRole()).thenReturn(UserRole.USER);
        String validToken = tokenService.generateToken(mockUser);

        // 2. Act
        String subject = tokenService.validateToken(validToken);

        // 3. Assert
        assertEquals(userId.toString(), subject, "Não validou corretamente o token");
    }

    @Test
    @DisplayName("Deve retornar string vazia para um token com assinatura inválida")
    void testValidateToken_InvalidSignature() {
        // 1. Arrange
        // Cria um token usando um algoritmo (chave secreta) DIFERENTE
        String tokenWithWrongSignature = JWT.create()
                .withIssuer("auth-api")
                .withSubject(UUID.randomUUID().toString()) // Usamos um UUID string aqui também
                .withClaim("role", "USER") // Adicionamos a claim de role para consistência
                .withExpiresAt(Instant.now().plusSeconds(300))
                .sign(wrongAlgorithm); // <-- Assinado com a chave errada

        // 2. Act
        String subject = tokenService.validateToken(tokenWithWrongSignature);

        // 3. Assert
        assertEquals("", subject, "Deveria retornar string vazia para assinatura inválida");
    }

    @Test
    @DisplayName("Deve retornar string vazia para um token expirado")
    void testValidateToken_ExpiredToken() {
        // 1. Arrange
        // Cria um token que JÁ EXPIROU (no passado)
        Instant expirationTimeInThePast = LocalDateTime.now()
                .minusHours(1) // 1 hora no passado
                .toInstant(ZoneOffset.of("-03:00"));

        String expiredToken = JWT.create()
                .withIssuer("auth-api")
                .withSubject(UUID.randomUUID().toString()) // Usamos um UUID string
                .withClaim("role", "USER") // Adicionamos a claim de role para consistência
                .withExpiresAt(expirationTimeInThePast) // <-- Data de expiração passada
                .sign(testAlgorithm); // <-- Assinatura correta

        // 2. Act
        String subject = tokenService.validateToken(expiredToken);

        // 3. Assert
        assertEquals("", subject, "Deveria retornar string vazia para token expirado");
    }

    @Test
    @DisplayName("Deve retornar string vazia para um token com Issuer (emissor) inválido")
    void testValidateToken_InvalidIssuer() {
        // 1. Arrange
        // Cria um token com um issuer diferente de "auth-api"
        String tokenWithWrongIssuer = JWT.create()
                .withIssuer("another-api") // <-- Issuer incorreto
                .withSubject(UUID.randomUUID().toString()) // Usamos um UUID string
                .withClaim("role", "USER") // Adicionamos a claim de role para consistência
                .withExpiresAt(Instant.now().plusSeconds(300))
                .sign(testAlgorithm); // <-- Assinatura correta

        // 2. Act
        String subject = tokenService.validateToken(tokenWithWrongIssuer);

        // 3. Assert
        assertEquals("", subject, "Deveria retornar string vazia para issuer inválido");
    }

    @Test
    @DisplayName("Deve retornar string vazia para um token mal formatado")
    void testValidateToken_MalformedToken() {
        // 1. Arrange
        String malformedToken = "isto.nao.e.um.token.jwt";

        // 2. Act
        String subject = tokenService.validateToken(malformedToken);

        // 3. Assert
        assertEquals("", subject, "Deveria retornar string vazia para token mal formatado");
    }
}






