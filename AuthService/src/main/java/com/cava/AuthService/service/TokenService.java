package com.cava.AuthService.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.cava.AuthService.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {


//    private static final String SECRET_KEY_STRING = generateSecretKey(32);

    private final Algorithm algorithm;


    public TokenService(@Value("${jwt.secret}") String secretKey) {
        this.algorithm = Algorithm.HMAC256(secretKey.getBytes(StandardCharsets.UTF_8));
    }


    public String generateToken(User user) {
        try {
//            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY_BYTES);
            return JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getId().toString())
                    .withClaim("role", user.getRole().name())
                    .withExpiresAt(geExpirationTime())
                    .sign(this.algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error creating token", exception);
        }
    }


    public String validateToken(String token) {
        try {
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException exception){
            return "";
        }
    }

    private Instant geExpirationTime() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

}
