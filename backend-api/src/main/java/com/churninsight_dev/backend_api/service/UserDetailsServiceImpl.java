// Implementación de UserDetailsService para la autenticación con Spring Security
package com.churninsight_dev.backend_api.service;

import com.churninsight_dev.backend_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    // Repositorio para acceder a los usuarios
    @Autowired
    private UserRepository loginRepository;

    /**
     * Carga los detalles del usuario por email para la autenticación.
     * Lanza excepción si el usuario no existe.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return loginRepository.findByEmail(email)
            .map(user -> org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER") // Asignar roles segun sea necesario
                .build())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + email));
    }
}
