package com.churninsight_dev.backend_api.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "age")
    private Integer age;

    @PositiveOrZero
    @Column(name = "watch_hours")
    private Double watchHours;

    @Column(name = "last_login_days")
    private Integer lastLoginDays;

    @PositiveOrZero
    @Column(name = "monthly_fee")
    private Double monthlyFee;

    @Min(1)
    @Column(name = "number_of_profiles")
    private Integer numberOfProfiles;

    @Column(name = "avg_watch_time_per_day")
    private Double avgWatchTimePerDay;

    @Column(name = "subscription_type")
    private String subscriptionType;

    @Column(name = "region")
    private String region;

    @Column(name = "device")
    private String device;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "favorite_genre")
    private String favoriteGenre;

    @Column(name = "gender")
    private String gender;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
