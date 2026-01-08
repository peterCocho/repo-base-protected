// Repositorio JPA para la entidad Login (usuario)
package com.churninsight.backend_api.repository;

import com.churninsight.backend_api.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<Login, Long> {
    /**
     * Busca usuario por nombre de usuario
     */
    Optional<Login> findByUserName(String userName);

    /**
     * Busca usuario por email
     */
    Optional<Login> findByEmail(String email);
}

