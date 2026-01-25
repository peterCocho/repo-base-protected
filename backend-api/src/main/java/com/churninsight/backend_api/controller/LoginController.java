package com.churninsight.backend_api.controller;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;


// Controlador principal para autenticación, registro y verificación de usuario
@RestController


// LoginController solo gestiona el endpoint protegido
public class LoginController {
    @GetMapping("/protected-test")
    public ResponseEntity<?> protectedTest() {
        return ResponseEntity.ok("Acceso permitido: JWT válido");
    }
}

