package com.example.backend.controller;

import com.example.backend.model.request.AuthRequest;
import com.example.backend.model.response.ApiResponse;
import com.example.backend.model.dto.UserDTO;
import com.example.backend.model.entity.User;
import com.example.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody AuthRequest request) {
        String token = authService.loginUser(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", token));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody AuthRequest request) {
        authService.registerUser(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "User info retrieved successfully",
                        new UserDTO(user)
                )
        );
    }
}

