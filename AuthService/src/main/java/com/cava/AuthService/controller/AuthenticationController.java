package com.cava.AuthService.controller;

import com.cava.AuthService.service.TokenService;
import com.cava.AuthService.model.*;
import com.cava.AuthService.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping ("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;


    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody AuthenticationDto data) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
            var auth = authenticationManager.authenticate(usernamePassword);

            var token = tokenService.generateToken((User) auth.getPrincipal());

            var userData = new UserLogin(((User) auth.getPrincipal()).getId().toString(), ((User) auth.getPrincipal()).getUsername(), ((User) auth.getPrincipal()).getEmail());
            var authData = new AuthData(token, "Bearer", 7200);
            return ResponseEntity.ok(new LoginResponseDTO(userData, authData));
        }catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuário não registrado ou senha inválida");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro inesperado: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody RegisterRequestDTO data){
        if(this.userRepository.existsUserByEmailOrUsername(data.email(), data.username())) return ResponseEntity.badRequest().body("Username or Email already used!");

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.username() ,data.email(), encryptedPassword);

        this.userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponseDTO(newUser.getId(), newUser.getUsername(), newUser.getEmail()));
    }
}
