package com.churninsight_dev.backend_api.controller;

import com.churninsight_dev.backend_api.dto.VerificationRequest;
import com.churninsight_dev.backend_api.dto.ErrorResponse;
import com.churninsight_dev.backend_api.dto.LoginResponse;
import com.churninsight_dev.backend_api.model.User;
import com.churninsight_dev.backend_api.model.VerificationCode;
import com.churninsight_dev.backend_api.repository.UserRepository;
import com.churninsight_dev.backend_api.repository.VerificationCodeRepository;
import com.churninsight_dev.backend_api.service.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import java.util.Date;

class VerificationControllerTest {
    @Mock
    private UserRepository loginRepository;
    @Mock
    private VerificationCodeRepository verificationCodeRepository;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private VerificationController verificationController;

    public VerificationControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void verifyCode_userNotFound_returnsNotFound() {
        VerificationRequest request = new VerificationRequest("no@existe.com", "123456");
        when(loginRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        ResponseEntity<?> response = verificationController.verifyCode(request);
        assertEquals(404, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void verifyCode_expiredCode_returnsBadRequest() {
        User user = new User();
        user.setId(1L);
        VerificationRequest request = new VerificationRequest("test@expira.com", "ABCDEF");
        when(loginRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        VerificationCode code = new VerificationCode("ABCDEF", user);
        code.setExpiryDate(new Date(System.currentTimeMillis() - 10000));
        when(verificationCodeRepository.findByLogin_Id(1L)).thenReturn(code);
        ResponseEntity<?> response = verificationController.verifyCode(request);
        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void verifyCode_validCode_returnsOk() {
        User user = new User();
        user.setId(2L);
        user.setEmail("ok@correo.com");
        user.setUserName("okuser");
        user.setStatus(0);
        VerificationRequest request = new VerificationRequest("ok@correo.com", "XYZ123");
        when(loginRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        VerificationCode code = new VerificationCode("XYZ123", user);
        code.setExpiryDate(new Date(System.currentTimeMillis() + 10000));
        when(verificationCodeRepository.findByLogin_Id(2L)).thenReturn(code);
        ResponseEntity<?> response = verificationController.verifyCode(request);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof LoginResponse);
    }
}
