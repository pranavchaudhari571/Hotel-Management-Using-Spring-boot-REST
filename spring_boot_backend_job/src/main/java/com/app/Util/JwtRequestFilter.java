package com.app.Util;

import com.app.entities.CustomUserDetails;
import io.jsonwebtoken.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import static com.app.service.AuthService.TOKEN_PREFIX;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${SECRET_KEY}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            // Check if the token is valid
            String tokenKey = TOKEN_PREFIX + token;// Extract token
            if (Boolean.TRUE.equals(redisTemplate.opsForValue().get(tokenKey))) {
                try {
                    Map<String, Object> claims = Jwts.parserBuilder()
                            .setSigningKey(jwtSecret)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

                    String username = (String) claims.get("sub");
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                    // Handle role and add the "ROLE_" prefix
                    if (claims.containsKey("role")) {
                        String role = (String) claims.get("role");
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));  // Add "ROLE_" prefix
                    }

                    Long adminId = (claims.get("adminId") instanceof Integer) ? ((Integer) claims.get("adminId")).longValue() : (Long) claims.get("adminId");

                    if (username != null) {
                        CustomUserDetails userDetails = new CustomUserDetails(username, authorities, adminId);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }

                } catch (ExpiredJwtException e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token has expired");
                    return;
                } catch (SignatureException e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT signature");
                    return;
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    return;
                }
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is invalid or expired");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}