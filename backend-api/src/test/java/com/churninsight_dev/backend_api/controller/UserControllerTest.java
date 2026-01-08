package com.churninsight_dev.backend_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private final LoginController loginController = new LoginController();

    @Test
    void protectedTest_returnsOk() {
        ResponseEntity<?> response = loginController.protectedTest();
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Acceso permitido: JWT válido", response.getBody());
    }
}
