package com.churninsight.backend_api.controller;
import com.churninsight.backend_api.dto.PredictionRequest;
import com.churninsight.backend_api.dto.PredictionResponse;
import com.churninsight.backend_api.repository.CustomerRepository;
import com.churninsight.backend_api.service.PredictionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST que expone los endpoints para la predicción de Churn.
 * Actúa como punto de entrada para las peticiones del Frontend y Postman.
 */
@RestController // IMPORTANTE: Sin esto, Spring no sabe que es una API
@RequestMapping("/api/v1/predictions")
@Tag(name = "Predictions", description = "Endpoints para el análisis de deserción de clientes y estadísticas")
@CrossOrigin(origins = "*") // Permite conexión desde el Frontend (React/Angular)
public class PredictionController {

    private final PredictionService predictionService;
    private final CustomerRepository customerRepository;

    /**
     * Inyección de dependencias por constructor.
     * Es la forma más limpia y recomendada en Spring Boot.
     */
    public PredictionController(PredictionService predictionService, CustomerRepository customerRepository){
        this.predictionService = predictionService;
        this.customerRepository = customerRepository;
    }


    /**
     * Endpoint POST que recibe los datos del cliente y devuelve la predicción.
     *
     * @param request Objeto DTO con los datos del cliente (validado automáticamente).
     * @return ResponseEntity con el resultado de la predicción y estado 200 OK.
     */

    @Operation(summary = "Procesar predicción", description = "Calcula el riesgo de fuga según el perfil del cliente.")
    @PostMapping("/predict")
    public ResponseEntity<PredictionResponse> getPrediction(@Valid @RequestBody PredictionRequest request){
        // Delegamos la logica de negocio al servicio
        PredictionResponse response = predictionService.processPrediction(request);
        // Devolvemos la respuesta con un estado HTTP 200 (ok)
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de prueba para verificar que la API está en línea.
     */
    @Operation(summary = "Verificar estado", description = "Confirma disponibilidad del servicio.")
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck(){
        return ResponseEntity.ok("API de ChurnInsight funcionando correctamente");
    }



// Metodo de estadisticas
    @Operation(summary = "Métricas globales", description = "Calcula totales y tasa de churn en base al histórico.")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        long total = customerRepository.count();
        long churned = customerRepository.countChurnedCustomers();
        // Estructura de respuesta para consumo del Dashboard
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_analizados", total);
        stats.put("clientes_en_riesgo", churned);
        stats.put("tasa_churn", total > 0 ? (double) churned / total : 0);

        return ResponseEntity.ok(stats);
    }
}
