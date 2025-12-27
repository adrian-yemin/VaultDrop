package com.example.backend.service;

import com.example.backend.config.security.JwtUtil;
import com.example.backend.exception.UsernameAlreadyExistsException;
import com.example.backend.model.request.AuthRequest;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerUser_success() {
        AuthRequest request = new AuthRequest("adrian", "password");

        when(userRepository.existsByUsername("adrian")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashed");

        authService.registerUser(request);

        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("adrian") &&
                        user.getPasswordHash().equals("hashed")
        ));
    }

    @Test
    void registerUser_usernameExists_throwsException() {
        AuthRequest request = new AuthRequest("adrian", "password");

        when(userRepository.existsByUsername("adrian")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class,
                () -> authService.registerUser(request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void loginUser_success_returnsToken() {
        AuthRequest request = new AuthRequest("adrian", "password");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(jwtUtil.generateToken("adrian")).thenReturn("jwt-token");

        String token = authService.loginUser(request);

        assertEquals("jwt-token", token);
    }

    @Test
    void loginUser_badCredentials_throwsException() {
        AuthRequest request = new AuthRequest("adrian", "wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class,
                () -> authService.loginUser(request));

        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void loginUser_notAuthenticated_throwsException() {
        AuthRequest request = new AuthRequest("adrian", "password");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        assertThrows(BadCredentialsException.class,
                () -> authService.loginUser(request));

        verify(jwtUtil, never()).generateToken(any());
    }

}
