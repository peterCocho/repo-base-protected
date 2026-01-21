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
        String email = request.getEmail();
        String code = request.getVerification();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse("Usuario no encontrado.")
            );
        }
        User user = userOpt.get();
        VerificationCode verificationCode = verificationCodeRepository.findByUser_Id(user.getId());
        if (verificationCode != null) {
            Date now = new Date();
            if (verificationCode.getExpiration() != null && now.after(verificationCode.getExpiration())) {
                verificationCodeRepository.delete(verificationCode);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse("El código de verificación ha expirado. Solicita uno nuevo.")
                );
            }
            if (verificationCode.getCode().equals(code)) {
                user.setStatus(true);
                userRepository.save(user);
                verificationCodeRepository.delete(verificationCode);
                return ResponseEntity.ok(
                    new LoginResponse("¡Tu cuenta ha sido verificada! Ahora puedes iniciar sesión.", user.getEmail(), user.getUserName(), user.getStatus())
                );
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse("Código de verificación incorrecto o expirado. Inténtalo de nuevo.")
        );
    }

    @PostMapping("/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Usuario no encontrado."));
        }
        User user = userOpt.get();
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        VerificationCode verificationCode = new VerificationCode(code, user);
        verificationCodeRepository.save(verificationCode);
        try {
            emailService.sendVerificationCode(user.getEmail(), code);
        } catch (Exception e) {
            verificationCodeRepository.delete(verificationCode);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("No se pudo enviar el correo de verificación: " + e.getMessage()));
        }
        return ResponseEntity.ok("Código de verificación enviado correctamente.");
    }
}
