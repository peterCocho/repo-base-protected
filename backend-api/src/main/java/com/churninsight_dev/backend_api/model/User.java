// Entidad que representa al usuario en el sistema
package com.churninsight_dev.backend_api.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.util.Set;
import java.util.Collections;
// ...existing code...
import lombok.Data;

@Data
@Entity
public class User {

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

    @ElementCollection(targetClass = Profile.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_profiles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "profile", nullable = false)
    private Set<Profile> profiles = Collections.singleton(Profile.ROLE_USER);

}
