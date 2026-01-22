package com.churninsight_dev.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {
    private Long total;
    private Long churn;
    private Long noChurn;
    private Double rate;
}