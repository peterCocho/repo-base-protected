package com.churninsight_dev.backend_api.controller;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import com.churninsight_dev.backend_api.dto.PredictionRequest;
import com.churninsight_dev.backend_api.dto.PredictionResponse;
import com.churninsight_dev.backend_api.repository.CustomerRepository;
import com.churninsight_dev.backend_api.service.PredictionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST que expone los endpoints para la predicción de Churn.
 * Actúa como punto de entrada para las peticiones del Frontend y Postman.
 */
@RestController // IMPORTANTE: Sin esto, Spring no sabe que es una API
@RequestMapping("/api/v1/predictions")
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
        long total = customerRepository.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total_analizados", total);
        // El campo churned y el método countChurnedCustomers ya no existen
        // Si necesitas mostrar otros datos, agrega la lógica aquí

        return ResponseEntity.ok(stats);
    }


    /**
     * (Opcional) Endpoint para predicción masiva por archivo CSV
     */
    /**
     * Endpoint para predicción masiva por archivo CSV
     */
    @PostMapping("/csv")
    public ResponseEntity<List<PredictionResponse>> predictFromCsv(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(predictionService.processCsvPrediction(file, authHeader));
    }
}
