package com.churninsight_dev.backend_api.controller;

import com.churninsight_dev.backend_api.dto.VerificationRequest;
import com.churninsight_dev.backend_api.dto.LoginResponse;
import com.churninsight_dev.backend_api.dto.ErrorResponse;
import com.churninsight_dev.backend_api.model.User;
import com.churninsight_dev.backend_api.model.VerificationCode;
import com.churninsight_dev.backend_api.repository.UserRepository;
import com.churninsight_dev.backend_api.repository.VerificationCodeRepository;
import com.churninsight_dev.backend_api.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RestController
public class VerificationController {
    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;

    @Autowired
    public VerificationController(UserRepository userRepository, VerificationCodeRepository verificationCodeRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.emailService = emailService;
    }

    @PostMapping(value = "/verification", consumes = "application/json")
    public ResponseEntity<?> verifyCode(@RequestBody VerificationRequest request) {
        // Extraer email y código de verificación de la solicitud
        String email = request.getEmail();
        String code = request.getVerification().toUpperCase();
        
        // Buscar el usuario por email
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Retornar error si el usuario no existe
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse("Usuario no encontrado.")
            );
        }
        
        // Obtener el usuario
        User user = userOpt.get();
        // Buscar el código de verificación asociado al usuario
        VerificationCode verificationCode = verificationCodeRepository.findByUser_Id(user.getId());
        if (verificationCode != null) {
            // Verificar si el código ha expirado
            Date now = new Date();
            if (verificationCode.getExpiration() != null && now.after(verificationCode.getExpiration())) {
                // Eliminar código expirado y retornar error
                verificationCodeRepository.delete(verificationCode);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse("El código de verificación ha expirado. Solicita uno nuevo.")
                );
            }
            
            // Verificar si el código coincide
            if (verificationCode.getCode().equals(code)) {
                // Activar la cuenta del usuario
                user.setStatus(true);
                userRepository.save(user);
                // Eliminar el código usado
                verificationCodeRepository.delete(verificationCode);
                // Retornar respuesta de éxito con información del usuario
                return ResponseEntity.ok(
                    new LoginResponse("¡Tu cuenta ha sido verificada! Ahora puedes iniciar sesión.", user.getEmail(), user.getUserName(), user.getStatus())
                );
            }
        }
        
        // Retornar error si el código es incorrecto o no existe
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse("Código de verificación incorrecto o expirado. Inténtalo de nuevo.")
        );
    }

    @PostMapping("/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody String email) {
        // Buscar el usuario por email
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Retornar error si el usuario no existe
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Usuario no encontrado."));
        }
        
        // Obtener el usuario
        User user = userOpt.get();
        // Generar un código de verificación único de 6 caracteres
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        // Crear entidad de código de verificación
        VerificationCode verificationCode = new VerificationCode(code, user);
        // Guardar el código en la base de datos
        verificationCodeRepository.save(verificationCode);
        
        // Intentar enviar el código por email
        try {
            emailService.sendVerificationCode(user.getEmail(), code);
        } catch (Exception e) {
            // Eliminar el código si falla el envío y retornar error
            verificationCodeRepository.delete(verificationCode);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("No se pudo enviar el correo de verificación: " + e.getMessage()));
        }
        
        // Retornar éxito si el email se envió correctamente
        return ResponseEntity.ok("Código de verificación enviado correctamente.");
    }
}
