package com.churninsight_dev.backend_api.controller;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import com.churninsight_dev.backend_api.dto.PredictionRequest;
import com.churninsight_dev.backend_api.dto.PredictionResponse;
import com.churninsight_dev.backend_api.dto.PredictionHistoryDTO;
import com.churninsight_dev.backend_api.repository.CustomerRepository;
import com.churninsight_dev.backend_api.repository.PredictionRepository;
import com.churninsight_dev.backend_api.service.PredictionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
// import java.util.List;
import java.util.Map;

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

    /**
     * Inyección de dependencias por constructor.
     * Es la forma más limpia y recomendada en Spring Boot.
     */
    public PredictionController(PredictionService predictionService, CustomerRepository customerRepository, PredictionRepository predictionRepository){
        this.predictionService = predictionService;
        // this.customerRepository = customerRepository;
        this.predictionRepository = predictionRepository;
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
        long clientesAnalizados = predictions.size();
        long churned = predictions.stream().filter(p -> p.getResultado() != null && p.getResultado().equalsIgnoreCase("churn")).count();
        double tasaChurn = clientesAnalizados > 0 ? (churned * 100.0) / clientesAnalizados : 0;
        double ingresosEnRiesgo = predictions.stream()
            .filter(p -> p.getResultado() != null && p.getResultado().equalsIgnoreCase("churn"))
            .mapToDouble(p -> p.getMonthlyFee() != null ? p.getMonthlyFee() : 0)
            .sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("clientes_analizados", clientesAnalizados);
        stats.put("porcentaje_churned", tasaChurn);
        stats.put("ingresos_en_riesgo", ingresosEnRiesgo);

        return ResponseEntity.ok(stats);
    }


    /**
     * Endpoint para predicción masiva por archivo CSV
     */
    @PreAuthorize("hasRole('PREMIUM')")
    @PostMapping("/csv")
    public ResponseEntity<List<PredictionResponse>> predictFromCsv(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(predictionService.processCsvPrediction(file, authHeader));
    }
}
