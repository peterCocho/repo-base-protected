package com.churninsight_dev.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionHistoryDTO {
    private String companyName;
    private String customerId;
    private String region;
    private String resultado;
    private Double probabilidad;
    private String factorPrincipal;
}
