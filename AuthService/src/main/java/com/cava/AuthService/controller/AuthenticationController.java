package com.cava.AuthService.controller;

import com.cava.AuthService.infra.security.TokenService;
import com.cava.AuthService.model.*;
import com.cava.AuthService.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
    public ResponseEntity login(@Valid @RequestBody AuthenticationDTO data) throws Exception {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody RegisterRequestDTO data){
        if(this.userRepository.existsUserByEmail(data.email())) return ResponseEntity.badRequest().body("User already exists");

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.username() ,data.email(), encryptedPassword);

        this.userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponseDTO(newUser.getId(), newUser.getUsername(), newUser.getEmail()));
    }
}
