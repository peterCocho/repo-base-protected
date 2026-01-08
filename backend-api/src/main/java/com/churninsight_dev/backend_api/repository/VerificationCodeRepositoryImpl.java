// Implementación alternativa del repositorio JPA para VerificationCode
package com.churninsight_dev.backend_api.repository;

import com.churninsight_dev.backend_api.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepositoryImpl extends JpaRepository<VerificationCode, Long> {
    /**
     * Busca el código de verificación por el ID de usuario (login).
     */
    VerificationCode findByLogin_Id(Long loginId);

    /**
     * Busca el código de verificación por el valor del código.
     */
    VerificationCode findByCode(String code);
}