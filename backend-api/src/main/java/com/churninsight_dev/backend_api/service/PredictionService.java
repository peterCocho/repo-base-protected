package com.churninsight_dev.backend_api.service;

import com.churninsight_dev.backend_api.dto.PredictionRequest;
import com.churninsight_dev.backend_api.dto.PredictionResponse;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface PredictionService {
    PredictionResponse processPrediction(PredictionRequest request);
    // Nueva sobrecarga para permitir pasar el token
    PredictionResponse processPrediction(PredictionRequest request, String jwtToken);
    
    List<PredictionResponse> processCsvPrediction(MultipartFile file);

    // Nueva sobrecarga para permitir pasar el token
    List<PredictionResponse> processCsvPrediction(MultipartFile file, String jwtToken);
}
