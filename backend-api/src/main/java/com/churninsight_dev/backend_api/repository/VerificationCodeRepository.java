// Repositorio JPA para la entidad VerificationCode
package com.churninsight_dev.backend_api.repository;

import com.churninsight_dev.backend_api.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    /**
     * Busca el código de verificación por el ID de usuario.
     */
    VerificationCode findByUser_Id(Long userId);

    /**
     * Busca el código de verificación por el valor del código.
     */
    VerificationCode findByCode(String code);
}
