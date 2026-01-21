// Punto de entrada para manejar errores de autenticación JWT
package com.churninsight_dev.backend_api.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    /**
     * Método que se ejecuta cuando falla la autenticación JWT.
     * Devuelve un error 401 en formato JSON.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        // Si el error es AccessDeniedException, personaliza el mensaje
        String uri = request.getRequestURI();
        if (uri != null && uri.contains("/api/v1/predictions/csv")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\":\"Herramienta no disponible. Actualiza a PREMIUM para tener acceso a predicciones múltiples.\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Token inválido o expirado\"}");
        }
    }
}
