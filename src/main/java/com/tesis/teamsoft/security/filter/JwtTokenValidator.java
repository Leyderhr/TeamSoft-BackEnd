package com.tesis.teamsoft.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.tesis.teamsoft.security.jwt.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

public class JwtTokenValidator extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtTokenValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);

            try {
                DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);

                String username = jwtUtils.extractUsername(decodedJWT);
                String stringAuthorities = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();

                Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(stringAuthorities);

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                Authentication authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                context.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(context);
            } catch (JWTVerificationException ex) {
                // Token inválido o expirado: responder 401 para que el cliente pueda
                // refrescar el token o redirigir al login (antes esto provocaba un 500).
                SecurityContextHolder.clearContext();
                writeUnauthorized(response, ex.getMessage());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        String safeMessage = message == null ? "Token inválido o expirado" : message.replace("\"", "'");
        response.getWriter().write(
                "{\"status\":401,\"error\":\"Token inválido o expirado\",\"message\":\"" + safeMessage + "\"}");
    }
}
