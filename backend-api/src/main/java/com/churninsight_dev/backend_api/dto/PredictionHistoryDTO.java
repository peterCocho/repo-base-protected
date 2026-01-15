package com.churninsight_dev.backend_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionHistoryDTO {
    public PredictionHistoryDTO(String companyName, String customerId, String resultado, Double probabilidad, String factorPrincipal, Double monthlyFee) {
        this.companyName = companyName;
        this.customerId = customerId;
        this.resultado = resultado;
        this.probabilidad = probabilidad;
        this.factorPrincipal = factorPrincipal;
        this.monthlyFee = monthlyFee;
    }
    private String companyName;
    private String customerId;
    private String resultado;
    private Double probabilidad;
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String factorPrincipal; // Solo para uso interno

    @JsonIgnore
    private Double monthlyFee; // Solo para uso interno

    @com.fasterxml.jackson.annotation.JsonProperty("custom_message")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private String customMessage;
}
