package com.cava.AuthService.model;

import java.util.UUID;

public record RegisterResponseDTO(UUID id, String username, String email) {
}
