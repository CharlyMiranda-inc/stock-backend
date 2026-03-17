package com.stock.stockbackend.controller;

import com.stock.stockbackend.dto.AuthResponse;
import com.stock.stockbackend.dto.LoginRequest;
import com.stock.stockbackend.dto.RegisterRequest;
import com.stock.stockbackend.dto.UserResponseDTO;
import com.stock.stockbackend.model.User;
import com.stock.stockbackend.repository.UserRepository;
import com.stock.stockbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest request) {
        authService.register(request);
    }

    @GetMapping("/me")
    public UserResponseDTO getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return new UserResponseDTO(user.getEmail(), user.getRole());
    }


}