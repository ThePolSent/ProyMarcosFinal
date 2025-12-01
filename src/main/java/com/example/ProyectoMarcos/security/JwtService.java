package com.example.ProyectoMarcos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // La clave secreta ahora se inyectará DIRECTAMENTE desde application.properties.
    // Esto asegura que la clave Base64 larga y segura se utilice.
    @Value("${jwt.secret}")
    private String SECRET_KEY; // << Valor por defecto eliminado.

    private static final long JWT_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7; // 7 días en milisegundos

    /**
     * Genera un token JWT con el correo (subject) y el rol (claim).
     * @param username El correo del usuario (usado como Subject).
     * @param role El rol del usuario (ADMIN/USER).
     * @return El token JWT generado.
     */
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // Agregamos el rol como una "claim"
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // Correo del usuario
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // --- Métodos de Extracción y Validación ---

    /**
     * Valida si un token es válido.
     * @param token El token a validar.
     * @param userDetails El objeto UserDetails del usuario autenticado.
     * @return true si el token es válido, false en caso contrario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Comprueba si el token ha expirado
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extrae la fecha de expiración del token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae el correo (Subject) del token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae una claim específica del token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extrae todas las claims (cuerpo) del token
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Devuelve la clave de firma decodificada
    private Key getSigningKey() {
        // La clave Base64 segura es leída de SECRET_KEY, y luego decodificada.
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        // Esto creará una clave de 256 bits, resolviendo el WeakKeyException.
        return Keys.hmacShaKeyFor(keyBytes);
    }
}