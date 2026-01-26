package com.churninsight_dev.backend_api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;



@Entity
@Table(name = "predictions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resultado;
    private Double probabilidad;
    private String factorPrincipal;

    @Column(name = "monthly_fee")
    private Double monthlyFee;

    @OneToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    @Column(name = "fecha_prediccion")
    private LocalDateTime fechaPrediccion;
}
