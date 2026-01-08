package com.churninsight.backend_api.model;


import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa a un cliente en el sistema.
 * Mapeada directamente con la tabla 'customers' en PostgreSQL.
 */
@Entity
@Table(name = "customers")
@Data // Genera Getters, Setters, toString, etc.
@NoArgsConstructor // Constructor vacio requerido por JPA
@AllArgsConstructor // Constructor con todos los campos
@Builder // Facilita la creacion de objetos
public class Customer {

    @Id
    @Column(name = "customer_id", length = 50)
    private String customerId;

    @Column(nullable = false)
    private Integer age;

    @Column(length = 20)
    private String gender;

    @Column(name = "subscription_type")
    private String subscriptionType;

    @Column(name = "watch_hours")
    private Double watchHours;

    @Column(name = "last_login_days")
    private Integer lastLoginDays;

    private String region;

    private String device;

    @Column(name = "monthly_fee")
    private Double monthlyFee;

    // Indica si el cliente ya ha cancelado (para entrenamiento)
    private Boolean churned;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "number_of_profiles")
    private Integer numberOfProfiles;

    @Column(name = "avg_watch_time_per_day")
    private Double avgWatchTimePerDay;

    @Column(name = "favorite_genre")
    private String favoriteGenre;
}

