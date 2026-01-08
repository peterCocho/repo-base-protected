package com.churninsight_dev.backend_api.controller;

import com.churninsight_dev.backend_api.dto.LoginDto;
import com.churninsight_dev.backend_api.dto.RegisterDto;
import com.churninsight_dev.backend_api.dto.AuthErrorResponse;
import com.churninsight_dev.backend_api.dto.LoginJwtResponse;
import com.churninsight_dev.backend_api.exception.EmailAlreadyExistsException;
import com.churninsight_dev.backend_api.exception.PasswordMismatchException;
import com.churninsight_dev.backend_api.model.User;
import com.churninsight_dev.backend_api.repository.UserRepository;
import com.churninsight_dev.backend_api.security.JwtUtil;
import com.churninsight_dev.backend_api.model.VerificationCode;
import com.churninsight_dev.backend_api.repository.VerificationCodeRepository;
import com.churninsight_dev.backend_api.service.EmailService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {
    private final UserRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;

    @Autowired
    public AuthController(UserRepository loginRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                         VerificationCodeRepository verificationCodeRepository, EmailService emailService) {
        this.loginRepository = loginRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.verificationCodeRepository = verificationCodeRepository;
        this.emailService = emailService;
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto) {
        String email = (loginDto.getEmail() == null) ? null : loginDto.getEmail().trim();
        String password = loginDto.getPassword();
        if (email == null || password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new AuthErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                    "Error de Login", "Email y contraseña requeridos.", "email/password", "CREDENTIALS_REQUIRED")
            );
        }
        Optional<User> userOpt = loginRepository.findByEmail(email);
        String genericLoginError = "Algunos de los datos ingresados es incorrecto. Vuelve a ingresarlos y revisa que estén bien escritos. ¿Olvidaste tu contraseña?";
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new AuthErrorResponse(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(),
                    "Error de Login", genericLoginError, "email/password", "INVALID_CREDENTIALS")
            );
        }
        User user = userOpt.get();
        if (user.getStatus() == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new AuthErrorResponse(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(),
                    "Error de Login", "Debes verificar tu cuenta antes de iniciar sesión.", "status", "ACCOUNT_NOT_VERIFIED")
            );
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new AuthErrorResponse(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(),
                    "Error de Login", genericLoginError, "email/password", "INVALID_CREDENTIALS")
            );
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("username", user.getUserName());
        claims.put("status", user.getStatus());
        String token = jwtUtil.generateToken(user.getEmail(), claims);
        return ResponseEntity.ok(
            new LoginJwtResponse("¡Bienvenido!", user.getEmail(), user.getUserName(), user.getStatus(), token)
        );
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterDto registerDto) {
        String email = (registerDto.getEmail() == null) ? null : registerDto.getEmail().trim();
        String username = (registerDto.getUserName() == null) ? null : registerDto.getUserName().trim();
        String password = registerDto.getPassword();
        String repeatPassword = registerDto.getRepeatPassword();
        String companyName = (registerDto.getCompanyName() == null) ? null : registerDto.getCompanyName().trim();
        if (email == null || !email.matches("^.+@.+\\..+")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new AuthErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                    "Error de Registro", "Correo inválido.", "email", "INVALID_EMAIL")
            );
        }
        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new AuthErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                    "Error de Registro", "El nombre de usuario es obligatorio.", "username", "USERNAME_REQUIRED")
            );
        }
        if (password == null || password.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new AuthErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                    "Error de Registro", "La contraseña es obligatoria.", "password", "PASSWORD_REQUIRED")
            );
        }
        if (companyName == null || companyName.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new AuthErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                    "Error de Registro", "El nombre de la empresa es obligatorio.", "companyName", "COMPANY_REQUIRED")
            );
        }
        if (!password.equals(repeatPassword)) {
            throw new PasswordMismatchException("Las contraseñas no coinciden. Asegúrate de que ambas sean iguales.");
        }
        Optional<User> existing = loginRepository.findByEmail(email);
        if (existing.isPresent()) {
            throw new EmailAlreadyExistsException("El email que has introducido ya está asociado a una cuenta.");
        }
        // Persistir usuario
        User user = new User();
        user.setEmail(email);
        user.setUserName(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(0);
        user.setCompanyName(companyName);
        User savedUser = loginRepository.save(user);

        // Eliminar código anterior si existe
        VerificationCode oldCode = verificationCodeRepository.findByLogin_Id(savedUser.getId());
        if (oldCode != null) {
            verificationCodeRepository.delete(oldCode);
        }

        // Generar y guardar nuevo código de verificación con expiración
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        VerificationCode verificationCode = new VerificationCode(code, savedUser);
        verificationCodeRepository.save(verificationCode);

        try {
            emailService.sendVerificationCode(savedUser.getEmail(), code);
        } catch (Exception e) {
            verificationCodeRepository.delete(verificationCode);
            loginRepository.delete(savedUser);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new AuthErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error de Registro", "No se pudo enviar el correo de verificación: " + e.getMessage(),
                    "email", "EMAIL_SEND_FAILED")
            );
        }

        return ResponseEntity.ok("Usuario registrado correctamente. Verifica tu correo.");
    }
}
