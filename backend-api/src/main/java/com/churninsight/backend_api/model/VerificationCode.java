// Entidad que representa el código de verificación para activar cuentas de usuario
package com.churninsight.backend_api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.Calendar;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
public class VerificationCode {

    // Tiempo de expiración del código en segundos
    private static final int EXPIRATION_SECONDS = 90; // 90 segundos

    // Identificador único del código
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Valor del código de verificación
    private String code;

    // Relación uno a uno con el usuario (Login)
    @OneToOne(targetEntity = Login.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "login_id")
    private Login login;

    // Fecha de expiración del código
    private Date expiryDate;

    /**
     * Constructor que inicializa el código, usuario y fecha de expiración
     */
    public VerificationCode(String code, Login login) {
        this.code = code;
        this.login = login;
        this.expiryDate = calculateExpiryDate(EXPIRATION_SECONDS);
    }

    /**
     * Calcula la fecha de expiración sumando segundos al momento actual
     */
    private Date calculateExpiryDate(int expiryTimeInSeconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.SECOND, expiryTimeInSeconds);
        return new Date(cal.getTime().getTime());
    }
}

