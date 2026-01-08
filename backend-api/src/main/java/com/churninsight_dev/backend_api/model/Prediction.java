package com.churninsight_dev.backend_api.model;

import jakarta.persistence.*;
import lombok.*;

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
    private String otrosDatos; // Si necesitas guardar el JSON completo

    @OneToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;
}
