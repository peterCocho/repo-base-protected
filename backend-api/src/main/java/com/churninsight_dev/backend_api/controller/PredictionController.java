package com.churninsight_dev.backend_api.controller;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;
import com.churninsight_dev.backend_api.dto.PredictionRequest;
import com.churninsight_dev.backend_api.dto.PredictionResponse;
import com.churninsight_dev.backend_api.dto.PredictionHistoryDTO;
import com.churninsight_dev.backend_api.service.PredictionService;
import com.churninsight_dev.backend_api.model.Profile;
import com.churninsight_dev.backend_api.model.User;
import com.churninsight_dev.backend_api.repository.UserRepository;
import com.churninsight_dev.backend_api.repository.PredictionRepository;
import com.churninsight_dev.backend_api.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Controlador REST que expone los endpoints para la predicción de Churn.
 * Actúa como punto de entrada para las peticiones del Frontend y Postman.
 */
@RestController // IMPORTANTE: Sin esto, Spring no sabe que es una API
@RequestMapping("/api/v1/predictions")
public class PredictionController {

    private final PredictionService predictionService;
    // private final CustomerRepository customerRepository;
    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * Inyección de dependencias por constructor.
     * Es la forma más limpia y recomendada en Spring Boot.
     */
    public PredictionController(PredictionService predictionService, PredictionRepository predictionRepository, UserRepository userRepository, JwtUtil jwtUtil){
        this.predictionService = predictionService;
        // this.customerRepository = customerRepository;
        this.predictionRepository = predictionRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }


    /**
     * Endpoint POST que recibe los datos del cliente y devuelve la predicción.
     *
     * @param request Objeto DTO con los datos del cliente (validado automáticamente).
     * @return ResponseEntity con el resultado de la predicción y estado 200 OK.
     */

    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM')")
    @PostMapping("/predict")
    public ResponseEntity<PredictionResponse> getPrediction(@Valid @RequestBody PredictionRequest request, @RequestHeader("Authorization") String authHeader){
        // Delegamos la logica de negocio al servicio, pasando el token
        PredictionResponse response = predictionService.processPrediction(request, authHeader);
        // Devolvemos la respuesta con un estado HTTP 200 (ok)
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de prueba para verificar que la API está en línea.
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck(){
        return ResponseEntity.ok("API de ChurnInsight funcionando correctamente");
    }
// Metodo de estadisticas
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        // Obtener predicciones solo de la empresa del usuario
        List<PredictionHistoryDTO> predictions = predictionRepository.findPredictionHistoryByUserEmail(email);
        
        // Log temporal para depuración: mostrar todos los main_factor de predicciones Churn
        System.out.println("[DEBUG] main_factor de predicciones Churn:");
        predictions.stream()
            .filter(p -> p.getResultado() != null && p.getResultado().equalsIgnoreCase("churn"))
            .forEach(p -> System.out.println(" - " + p.getFactorPrincipal()));
        
        long clientesAnalizados = predictions.size();
        long churned = predictions.stream().filter(p -> p.getResultado() != null && p.getResultado().equalsIgnoreCase("churn")).count();
        double tasaChurn = clientesAnalizados > 0 ? (churned * 100.0) / clientesAnalizados : 0;
        double ingresosEnRiesgo = predictions.stream()
            .filter(p -> p.getResultado() != null && p.getResultado().equalsIgnoreCase("churn"))
            .mapToDouble(p -> p.getMonthlyFee() != null ? p.getMonthlyFee() : 0)
            .sum();

        long cantidadNoChurn = predictions.stream().filter(p -> p.getResultado() != null && p.getResultado().equalsIgnoreCase("no churn")).count();
        // double porcentajeNoChurn = clientesAnalizados > 0 ? (cantidadNoChurn * 100.0) / clientesAnalizados : 0;

        // Porcentaje/cantidad de veces que cada variable fue main_factor en predicciones Churn
        Map<String, Long> cantidadSalida = new LinkedHashMap<>();
        long churnCount = predictions.stream().filter(p -> p.getResultado() != null && p.getResultado().equalsIgnoreCase("churn")).count();
        cantidadSalida.put("watch_hours", predictions.stream().filter(p -> {
            if (p.getResultado() == null || !p.getResultado().equalsIgnoreCase("churn") || p.getFactorPrincipal() == null) return false;
            String factor = p.getFactorPrincipal().toLowerCase();
            return factor.endsWith("watch_hours");
        }).count());
        cantidadSalida.put("last_login_days", predictions.stream().filter(p -> {
            if (p.getResultado() == null || !p.getResultado().equalsIgnoreCase("churn") || p.getFactorPrincipal() == null) return false;
            String factor = p.getFactorPrincipal().toLowerCase();
            return factor.endsWith("last_login_days");
        }).count());
        cantidadSalida.put("monthly_fee", predictions.stream().filter(p -> {
            if (p.getResultado() == null || !p.getResultado().equalsIgnoreCase("churn") || p.getFactorPrincipal() == null) return false;
            String factor = p.getFactorPrincipal().toLowerCase();
            return factor.endsWith("monthly_fee");
        }).count());
        cantidadSalida.put("number_of_profiles", predictions.stream().filter(p -> {
            if (p.getResultado() == null || !p.getResultado().equalsIgnoreCase("churn") || p.getFactorPrincipal() == null) return false;
            String factor = p.getFactorPrincipal().toLowerCase();
            return factor.endsWith("number_of_profiles");
        }).count());

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("clientes_analizados", clientesAnalizados);
        stats.put("tasa_churn", tasaChurn);
        stats.put("ingresos_en_riesgo", ingresosEnRiesgo);
        stats.put("cantidad_churn", churned);
        stats.put("cantidad_retenidos", cantidadNoChurn);
        stats.put("cantidad_salida_variables", cantidadSalida);

        // Top 5 predicciones con mayor probabilidad (al final)
        List<Map<String, Object>> top5 = predictions.stream()
            .sorted((a, b) -> Double.compare(b.getProbabilidad() != null ? b.getProbabilidad() : 0, a.getProbabilidad() != null ? a.getProbabilidad() : 0))
            .limit(5)
            .map(p -> {
                Map<String, Object> row = new HashMap<>();
                row.put("customer_id", p.getCustomerId());
                row.put("probabilidad", p.getProbabilidad());
                row.put("mensaje", p.getCustomMessage());
                return row;
            })
            .toList();
        stats.put("top5_predicciones", top5);

        return ResponseEntity.ok(stats);
    }


    /**
     * Endpoint para predicción masiva por archivo CSV
     */
    @PostMapping("/csv")
    public ResponseEntity<?> predictFromCsv(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String authHeader) {
        // Extraer email del token
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);

        // Verificar si el usuario tiene rol premium
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !userOpt.get().getProfiles().contains(Profile.ROLE_PREMIUM)) {
            return ResponseEntity.status(402).body(Map.of(
                "error", "Acceso denegado",
                "message", "Esta función requiere una suscripción premium. Actualiza tu cuenta para cargar predicciones múltiples.",
                "upgradeUrl", "/upgrade"
            ));
        }

        return ResponseEntity.ok(predictionService.processCsvPrediction(file, authHeader));
    }
}
