// Repositorio JPA para la entidad Login (usuario)
package com.churninsight_dev.backend_api.repository;

import com.churninsight_dev.backend_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Busca usuario por nombre de usuario
     */
    Optional<User> findByUserName(String userName);

    /**
     * Busca usuario por email
     */
    Optional<User> findByEmail(String email);
}

