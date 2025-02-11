package com.app.Util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Value("${SECRET_KEY}")
    private String jwtSecret;
    ; // Load securely from environment

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        // Check if the header contains Bearer token
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);  // Extract the token

            try {
                // Parse the token and extract claims
                Map<String, Object> claims = Jwts.parserBuilder()
                        .setSigningKey(jwtSecret)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                // Extract username (subject)
                String username = (String) claims.get("sub");

                // Extract roles if available
                List<GrantedAuthority> authorities = new ArrayList<>();
                if (claims.containsKey("roles")) {
                    List<String> roles = (List<String>) claims.get("roles");
                    for (String role : roles) {
                        authorities.add(new SimpleGrantedAuthority(role));
                    }
                }

                // Create an authentication token and set it in the SecurityContext
                if (username != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            } catch (ExpiredJwtException e) {
                logger.error("JWT token has expired", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token has expired");
                return; // If expired, stop the filter chain here
            } catch (SignatureException e) {
                logger.error("Invalid JWT signature", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT signature");
                return; // If invalid signature, stop the filter chain here
            } catch (Exception e) {
                logger.error("Error parsing JWT token", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }
        } else {
            // Log and proceed if the Authorization header is missing or malformed
            logger.warn("Authorization header is missing or malformed");
        }

        // Proceed with the filter chain
        chain.doFilter(request, response);
    }
}
