package com.churninsight_dev.backend_api.controller;

import com.churninsight_dev.backend_api.model.Profile;
import com.churninsight_dev.backend_api.model.User;
import com.churninsight_dev.backend_api.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para manejar pagos y suscripciones premium.
 * Proporciona endpoints para crear órdenes de pago, capturar pagos y confirmar upgrades a premium.
 * Actualmente simula integraciones con PayPal para desarrollo.
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    
    private final UserRepository userRepository;

    /**
     * Constructor para inyección de dependencias.
     * @param userRepository Repositorio para acceder a los datos de usuarios.
     */
    public PaymentController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Endpoint para crear una orden de pago.
     * Simula la creación de una orden de pago con PayPal y devuelve la URL de aprobación.
     * @param request Mapa con el email del usuario.
     * @return ResponseEntity con la URL de aprobación y el ID de la orden, o error si falta el email.
     */
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

    /**
     * Endpoint para capturar un pago aprobado.
     * Simula la captura del pago basado en el ID de la orden.
     * @param orderId ID de la orden de pago.
     * @return ResponseEntity con mensaje de éxito o error.
     */
    @PostMapping("/capture/{orderId}")
    public ResponseEntity<?> capturePayment(@PathVariable String orderId) {
        // Simular captura de pago
        if (orderId.startsWith("simulated_order_")) {
            return ResponseEntity.ok("Pago capturado exitosamente");
        }
        return ResponseEntity.status(500).body("Error al capturar pago");
    }

    /**
     * Endpoint para confirmar y asignar el rol premium al usuario.
     * Actualiza el perfil del usuario para incluir ROLE_PREMIUM si no lo tiene.
     * @param request DTO con el email del usuario.
     * @return ResponseEntity con mensaje de éxito o error si el usuario no existe.
     */
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

    /**
     * DTO interno para recibir la confirmación de pago.
     * Contiene el email del usuario que confirma el pago.
     */
    public static class PaymentConfirmationRequest {
        private String email;
        
        /**
         * Obtiene el email.
         * @return El email del usuario.
         */
        public String getEmail() { return email; }
        
        /**
         * Establece el email.
         * @param email El email del usuario.
         */
        public void setEmail(String email) { this.email = email; }
    }
}
