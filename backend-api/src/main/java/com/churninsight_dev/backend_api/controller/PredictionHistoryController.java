package com.churninsight_dev.backend_api.controller;

import com.churninsight_dev.backend_api.dto.PredictionHistoryDTO;
import com.churninsight_dev.backend_api.repository.PredictionRepository;
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
        return predictionRepository.findPredictionHistory();
    }
}
