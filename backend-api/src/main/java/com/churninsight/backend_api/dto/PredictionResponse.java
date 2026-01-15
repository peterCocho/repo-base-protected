package com.churninsight.backend_api.dto;

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

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("prevision")
    private String prevision; // Ejemplo: "Va a cancelar" / "Va a continuar"

    @JsonProperty("probabilidad")
    private Double probabilidad; // Ejemplo: 0.85

    /* @JsonProperty("status")
    private String status; // Ejemplo: "Success" */
}
