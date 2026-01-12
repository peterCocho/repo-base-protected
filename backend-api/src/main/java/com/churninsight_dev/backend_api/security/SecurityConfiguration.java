// Configuración de seguridad para la API: define filtros, endpoints públicos y protección JWT
package com.churninsight_dev.backend_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfiguration {


    /**
     * Bean para codificar contraseñas usando BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * Configura la cadena de filtros de seguridad:
     * - Desactiva CSRF
     * - Define endpoints públicos
     * - Protege el resto con JWT
     * - Usa el filtro y entrypoint personalizados
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter, JwtAuthEntryPoint jwtAuthEntryPoint) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/register", "/login", "/verification", "/css/**", "/js/**").permitAll()
                .requestMatchers("/api/users/admin").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    // Eliminado configureGlobal para evitar ciclo de dependencias y seguir buenas prácticas Spring Security 6
    
}
