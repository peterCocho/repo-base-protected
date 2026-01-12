package com.churninsight_dev.backend_api.controller;

import com.churninsight_dev.backend_api.model.Profile;
import com.churninsight_dev.backend_api.model.User;
import com.churninsight_dev.backend_api.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final UserRepository userRepository;

    public PaymentController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPremium(@RequestBody PaymentConfirmationRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.getProfiles().add(Profile.ROLE_PREMIUM);
            userRepository.save(user);
            return ResponseEntity.ok("Rol premium asignado");
        }
        return ResponseEntity.badRequest().body("Usuario no encontrado");
    }

    // DTO para recibir la confirmación de pago
    public static class PaymentConfirmationRequest {
        private String email;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
