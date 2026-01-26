package com.churninsight_dev.backend_api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    // Clave secreta para firmar el JWT
    private final String SECRET_KEY = "clave_jwt_secreta_super_segura__1234567890";
    // Tiempo de expiración del token (1 hora)
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hora

    /**
     * Obtiene la clave de firma para el JWT usando el algoritmo HMAC SHA.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Genera un token JWT con los claims y el sujeto especificado.
     * @param subject El sujeto (usualmente el email del usuario)
     * @param claims Los datos adicionales a incluir en el token
     * @return El token JWT generado
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae el nombre de usuario (sujeto) del token JWT.
     * @param token El token JWT
     * @return El sujeto (usualmente el email)
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Verifica si el token JWT es válido y no ha expirado.
     * @param token El token JWT
     * @return true si es válido, false si no
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica si el token ha expirado.
     * @param claims Los claims del token
     * @return true si ha expirado, false si no
     */
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    /**
     * Extrae todos los claims del token JWT.
     * @param token El token JWT
     * @return Los claims extraídos
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Devuelve los claims como un Map a partir del token JWT.
     * @param token El token JWT
     * @return Un Map con los claims
     */
    public Map<String, Object> getClaims(String token) {
        return new HashMap<>(extractAllClaims(token));
    }
}
