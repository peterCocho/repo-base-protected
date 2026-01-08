// Entidad que representa al usuario en el sistema
package com.churninsight.backend_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Login {

    // Identificador único del usuario
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre de usuario
    private String userName;
    // Email del usuario
    private String email;
    // Contraseña encriptada
    private String password;
    // Estado de verificación: 0 = No Verificado, 1 = Verificado
    private int status = 0;
    // 0 = No Verificado
    // 1 = Verificado
    // Nuevo campo: nombre de la empresa
    private String companyName;

}
