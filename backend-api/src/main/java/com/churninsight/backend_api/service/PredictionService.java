package com.churninsight.backend_api.service;

import com.churninsight.backend_api.dto.PredictionRequest;
import com.churninsight.backend_api.dto.PredictionResponse;

public interface PredictionService {
    PredictionResponse processPrediction(PredictionRequest request);
}
