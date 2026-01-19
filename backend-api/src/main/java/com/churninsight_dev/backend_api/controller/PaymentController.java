package com.churninsight_dev.backend_api.controller;

import com.churninsight_dev.backend_api.model.Profile;
import com.churninsight_dev.backend_api.model.User;
import com.churninsight_dev.backend_api.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final UserRepository userRepository;

    public PaymentController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null) {
            return ResponseEntity.badRequest().body("Email requerido");
        }

        // Simular creación de orden de pago
        String orderId = "simulated_order_" + System.currentTimeMillis();
        String approvalUrl = "http://localhost:5173/payment/success?email=" + email + "&orderId=" + orderId;

        return ResponseEntity.ok(Map.of("approvalUrl", approvalUrl, "orderId", orderId));
    }

    @PostMapping("/capture/{orderId}")
    public ResponseEntity<?> capturePayment(@PathVariable String orderId) {
        // Simular captura de pago
        if (orderId.startsWith("simulated_order_")) {
            return ResponseEntity.ok("Pago capturado exitosamente");
        }
        return ResponseEntity.status(500).body("Error al capturar pago");
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPremium(@RequestBody PaymentConfirmationRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!user.getProfiles().contains(Profile.ROLE_PREMIUM)) {
                user.getProfiles().add(Profile.ROLE_PREMIUM);
                userRepository.save(user);
            }
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
