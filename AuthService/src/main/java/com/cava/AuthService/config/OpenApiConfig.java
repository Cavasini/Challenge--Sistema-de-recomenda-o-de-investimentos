package com.cava.AuthService.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
// 1. Define o esquema de segurança (Security Scheme)
@SecurityScheme(
        name = "bearerAuth", // Um nome para o esquema, pode ser qualquer um
        type = SecuritySchemeType.HTTP, // Tipo HTTP
        scheme = "bearer", // Esquema "Bearer" (para JWT)
        bearerFormat = "JWT" // Formato do token
)
// 2. (Opcional) Define a informação da API
@OpenAPIDefinition(
        info = @Info(title = "Minha API", version = "v1"),
        // 3. (Opcional) Aplica o requisito de segurança globalmente
        //    Isso faz com que todos os endpoints no Swagger UI
        //    apareçam como "protegidos" (com cadeado).
        security = { @SecurityRequirement(name = "bearerAuth") }
)
public class OpenApiConfig {
    // Esta classe pode ficar vazia, ela serve apenas para guardar as anotações
}
