package com.churninsight_dev.backend_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa la respuesta de la predicción enviada al cliente (Frontend).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PredictionResponse {

    @JsonProperty("prediction")
    private String prediction; // Ejemplo: "Churn" o "No Churn"

    @JsonProperty("probability")
    private Double probability; // Ejemplo: 0.85

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String mainFactor; // Solo para uso interno, no se serializa en la respuesta

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Double monthlyFee; // Solo para uso interno

    @JsonProperty("custom_message")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private String customMessage;

    @JsonProperty("customer_id")
    private String customerId;
}
