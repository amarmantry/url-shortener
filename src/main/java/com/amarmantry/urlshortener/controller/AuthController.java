package com.amarmantry.urlshortener.controller;

import com.amarmantry.urlshortener.dto.auth.AuthResponse;
import com.amarmantry.urlshortener.dto.auth.LoginRequest;
import com.amarmantry.urlshortener.dto.auth.RegisterRequest;
import com.amarmantry.urlshortener.model.User;
import com.amarmantry.urlshortener.repository.UserRepository;
import com.amarmantry.urlshortener.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(token)
                        .username(user.getUsername())
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String token = jwtUtil.generateToken(request.getUsername());

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(token)
                        .username(request.getUsername())
                        .build()
        );
    }
}