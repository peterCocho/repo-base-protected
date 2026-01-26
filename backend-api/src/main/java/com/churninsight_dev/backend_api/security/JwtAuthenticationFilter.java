// Filtro para validar el JWT en cada petición HTTP
package com.churninsight_dev.backend_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Utilidad para operaciones con JWT
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Método principal que intercepta cada petición y valida el JWT.
     * Si el token es válido, se establece la autenticación en el contexto de seguridad.
     */
    @Override
    protected void doFilterInternal(
            @org.springframework.lang.NonNull HttpServletRequest request,
            @org.springframework.lang.NonNull HttpServletResponse response,
            @org.springframework.lang.NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        // Extraer el token JWT del header Authorization
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception ignored) {}
        }

        // Validar el token y establecer autenticación si es válido
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.isTokenValid(jwt)) {
                // Extraer roles del token
                Map<String, Object> claims = jwtUtil.getClaims(jwt);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                Object rolesObj = claims.get("roles");
                System.out.println("[DEBUG] JWT roles claim: " + rolesObj);
                if (rolesObj instanceof List<?> rolesList) {
                    for (Object role : rolesList) {
                        String roleStr = role.toString();
                        if (roleStr.startsWith("ROLE_")) {
                            roleStr = roleStr.substring(5);
                        }
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleStr));
                    }
                } else if (rolesObj instanceof String roleStr) {
                    if (roleStr.startsWith("ROLE_")) {
                        roleStr = roleStr.substring(5);
                    }
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + roleStr));
                }
                System.out.println("[DEBUG] Authorities asignados: " + authorities);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        new User(username, "", authorities), null, authorities
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
