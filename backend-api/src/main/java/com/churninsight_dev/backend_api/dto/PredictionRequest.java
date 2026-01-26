package com.churninsight_dev.backend_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRequest {

    @NotBlank(message = "El customer_id es obligatorio")
    @JsonProperty("customer_id")
    private String customerId;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad debe ser un número positivo")
    @JsonProperty("age")
    private Integer age;

    @NotNull(message = "Las horas de visualización son obligatorias")
    @Min(value = 0, message = "Las horas de visualización deben ser un número positivo")
    @JsonProperty("watch_hours")
    private Double watchHours;

    @NotNull(message = "Los días desde el último login son obligatorios")
    @Min(value = 0, message = "Los días desde el último login deben ser un número positivo")
    @JsonProperty("last_login_days")
    private Integer lastLoginDays;

    @NotNull(message = "El cargo mensual es obligatorio")
    @Positive(message = "El cargo mensual debe ser un valor positivo")
    @JsonProperty("monthly_fee")
    private Double monthlyFee;

    @NotNull(message = "El número de perfiles es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 perfil")
    @JsonProperty("number_of_profiles")
    private Integer numberOfProfiles;

    @NotNull(message = "El promedio de horas diarias es obligatorio")
    @Min(value = 0, message = "El promedio de horas diarias debe ser un número positivo")
    @JsonProperty("avg_watch_time_per_day")
    private Double avgWatchTimePerDay;

    @NotBlank(message = "El tipo de suscripción es obligatorio")
    @JsonProperty("subscription_type")
    private String subscriptionType;

    @NotBlank(message = "La región es obligatoria")
    @JsonProperty("region")
    private String region;

    @NotBlank(message = "El dispositivo es obligatorio")
    @JsonProperty("device")
    private String device;

    @NotBlank(message = "El método de pago es obligatorio")
    @JsonProperty("payment_method")
    private String paymentMethod;

    @NotBlank(message = "El género favorito es obligatorio")
    @JsonProperty("favorite_genre")
    private String favoriteGenre;

    @NotBlank(message = "El género es obligatorio")
    @JsonProperty("gender")
    private String gender;
}
