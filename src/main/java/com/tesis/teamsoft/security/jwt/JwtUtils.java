package com.tesis.teamsoft.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtils {

    @Value("${security.jwt.key.private}")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    @Value("${security.jwt.expiration-ms:1800000}")
    private long expirationMs;

    @Autowired
    private UserDetailsService userDetailsService;

    // Generar token a partir de Authentication
    public String createToken(Authentication authentication) {
        if (this.privateKey == null || this.privateKey.length() < 32) {
            log.warn("JWT secret key is not configured or too short; use a 256-bit (32+ chars) secret for HS256");
        }

        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        Object principal = authentication.getPrincipal();
        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();

        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return JWT.create()
                .withIssuer(this.userGenerator)
                .withSubject(username)
                .withClaim("authorities", authorities)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationMs))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
    }

    // Generar token a partir de username (usado en refresh)
    public String createToken(String username) {
        if (this.privateKey == null || this.privateKey.length() < 32) {
            log.warn("JWT secret key is not configured or too short");
        }

        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        String authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return JWT.create()
                .withIssuer(this.userGenerator)
                .withSubject(username)
                .withClaim("authorities", authorities)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationMs))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
    }

    // Valida token y devuelve DecodedJWT o lanza JWTVerificationException
    public DecodedJWT validateToken(String token) throws JWTVerificationException {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();

            return verifier.verify(token);
        } catch (JWTVerificationException exception) {
            log.warn("JWT validation failed: {}", exception.getMessage());
            throw exception; // preserve original type and message
        }
    }

    public String extractUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }

    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName) {
        return decodedJWT.getClaim(claimName);
    }
}