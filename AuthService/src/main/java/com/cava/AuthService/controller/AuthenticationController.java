package com.cava.AuthService.controller;

import com.cava.AuthService.infra.security.TokenService;
import com.cava.AuthService.model.AuthenticationDto;
import com.cava.AuthService.model.LoginResponseDTO;
import com.cava.AuthService.model.RegisterDTO;
import com.cava.AuthService.model.Users;
import com.cava.AuthService.repository.UserRepository;
import com.cava.AuthService.service.AuthorizationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthorizationService authorizationService;

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody AuthenticationDto data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Users) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody RegisterDTO data){
        if(this.userRepository.findByEmail(data.login()) != null) return ResponseEntity.badRequest().body("User already exists");

        authorizationService.registerUser(data);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/validar")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestBody TokenDTO tokenDTO) {
        if (tokenDTO.token() == null || tokenDTO.token().isEmpty()) {
            return ResponseEntity.badRequest().body(new TokenValidationResponse(false, "Token ausente ou vazio", null));
        }

        var decodedToken = tokenService.validateToken(tokenDTO.token());

        if (decodedToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenValidationResponse(false, "Token inválido ou expirado", null));
        }

        return ResponseEntity.ok(
                new TokenValidationResponse(true, "Token válido", decodedToken)
        );
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity getById(@PathVariable String userId){
        UserResponseDTO user = authorizationService.getUserById(userId);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
}