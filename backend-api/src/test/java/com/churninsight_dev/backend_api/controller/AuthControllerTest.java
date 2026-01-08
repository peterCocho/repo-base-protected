package com.churninsight_dev.backend_api.controller;

import com.churninsight_dev.backend_api.dto.LoginDto;
import com.churninsight_dev.backend_api.dto.LoginJwtResponse;
import com.churninsight_dev.backend_api.model.User;
import com.churninsight_dev.backend_api.repository.UserRepository;
import com.churninsight_dev.backend_api.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;

class AuthControllerTest {
    @Mock
    private UserRepository loginRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthController authController;

    public AuthControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginUser_invalidCredentials_returnsUnauthorized() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("wrongpassword");
        when(loginRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        ResponseEntity<?> response = authController.loginUser(loginDto);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void loginUser_validCredentials_returnsJwt() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("user@example.com");
        loginDto.setPassword("password");
        User user = new User();
        user.setEmail("user@example.com");
        user.setUserName("usuario");
        user.setPassword("hashed");
        user.setStatus(1);
        when(loginRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyMap())).thenReturn("token123");
        ResponseEntity<?> response = authController.loginUser(loginDto);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof LoginJwtResponse);
    }
}
