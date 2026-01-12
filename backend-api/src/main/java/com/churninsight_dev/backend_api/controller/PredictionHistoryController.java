package com.churninsight_dev.backend_api.controller;

import com.churninsight_dev.backend_api.dto.PredictionHistoryDTO;
import com.churninsight_dev.backend_api.repository.PredictionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/predictions/history")
public class PredictionHistoryController {
    private final PredictionRepository predictionRepository;

    public PredictionHistoryController(PredictionRepository predictionRepository) {
        this.predictionRepository = predictionRepository;
    }

    @GetMapping
    public List<PredictionHistoryDTO> getPredictionHistory() {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        // Aquí deberías obtener la empresa del usuario autenticado (por email)
        // Por simplicidad, supongamos que el método predictionRepository.findPredictionHistoryByUserEmail existe
        return predictionRepository.findPredictionHistoryByUserEmail(email);
    }
}
