package com.cava.AuthService.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
        @NotBlank(message = "O username não pode estar vazio")
        String username ,
        @NotBlank(message = "O email não pode estar vazio")
        @Email(message = "O formato do email é inválido")
        String email,
        @NotBlank(message = "A senha não pode estar vazia")
        String password) {
}
