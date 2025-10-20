package com.cava.AuthService.model;

public record AuthData(String accessToken, String tokenType, Integer expiresIn) {
}
