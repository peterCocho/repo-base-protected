package com.churninsight.backend_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de predicción.
 * Incluye validaciones, mapeo JSON y documentación para Swagger.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo de entrada para los datos técnicos del cliente a analizar")
public class PredictionRequest {

    @Schema(example = "CLI-001", description = "Identificador único del cliente")
    @NotBlank(message = "El customer_id es obligatorio")
    @JsonProperty("customer_id")
    private String customerId;

    @Schema(example = "30", description = "Edad del cliente (entre 0 y 120 años)")
    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 120, message = "Por favor, ingrese una edad válida")
    @JsonProperty("age")
    private Integer age;

    @Schema(example = "Male", description = "Género del cliente (Male, Female, Other)")
    @NotBlank(message = "El género es obligatorio")
    @JsonProperty("gender")
    private String gender;

    @Schema(example = "Premium", description = "Tipo de plan: Basic, Standar o Premium")
    @NotBlank(message = "El tipo de suscripción es obligatorio")
    @JsonProperty("subscription_type")
    private String subscriptionType;

    @Schema(example = "120.5", description = "Total de horas acumuladas de visualización")
    @NotNull(message = "Las horas de visualización son obligatorias")
    @Min(0)
    @JsonProperty("watch_hours")
    private Double watchHours;

    @Schema(example = "5", description = "Días transcurridos desde que el cliente inició sesión por última vez")
    @NotNull(message = "Los días desde el último login son obligatorios")
    @Min(0)
    @JsonProperty("last_login_days")
    private Integer lastLoginDays;

    @Schema(example = "LATAM", description = "Región geográfica del cliente")
    @NotBlank(message = "La región es obligatoria")
    @JsonProperty("region")
    private String region;

    @Schema(example = "Smart TV", description = "Días transcurridos desde que el cliente inició sesión por última vez")
    @JsonProperty("device")
    private String device;

    @Schema(example = "15.99", description = "Monto de la tarifa mensual pagada por el cliente")
    @Positive(message = "El cargo mensual debe ser un valor positivo")
    @JsonProperty("monthly_fee")
    private Double monthlyFee;

    @Schema(example = "Credit Card", description = "Método de pago registrado")
    @NotBlank(message = "El método de pago es obligatorio")
    @JsonProperty("payment_method")
    private String paymentMethod;

    @Schema(example = "3", description = "Número de perfiles creados en la cuenta")
    @NotNull(message = "El número de perfiles es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 perfil")
    @JsonProperty("number_of_profiles")
    private Integer numberOfProfiles;

    @Schema(example = "4.5", description = "Promedio de horas de uso diario del servicio")
    @NotNull(message = "El promedio de tiempo diario es obligatorio")
    @Min(0)
    @JsonProperty("avg_watch_time_per_day")
    private Double avgWatchTimePerDay;

    @Schema(example = "Sci-Fi", description = "Género de contenido preferido por el cliente")
    @NotBlank(message = "El género favorito es obligatorio")
    @JsonProperty("favorite_genre")
    private String favoriteGenre;
}
