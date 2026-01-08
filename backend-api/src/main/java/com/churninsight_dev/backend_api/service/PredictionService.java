package com.churninsight_dev.backend_api.service;

import com.churninsight_dev.backend_api.dto.PredictionRequest;
import com.churninsight_dev.backend_api.dto.PredictionResponse;

public interface PredictionService {
    PredictionResponse processPrediction(PredictionRequest request);
}
