package com.app.service;

import com.app.dao.UserRepository;
import com.app.entities.Role;
import com.app.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
@XSlf4j
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Key SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode("ZGpGVHVtMkZ3c3Y4TmJqU2RSbVJIV1lLcndITjVXSjdTY0FGVTF4UUV3TExIVm5WdUJuT0xZR3U="));


    // Generate JWT Token with username and role
    public String generateToken(String username, Role role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role.name())  // Add role as claim in token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(SECRET_KEY)
                .compact();
    }

    // Register a user and assign role (if not already assigned)
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        // Assign 'USER' role by default if not provided
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
       // user.setRole(Role.USER);
        return userRepository.save(user);
    }

    // Authenticate user and return user details
    public User authenticate(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                log.info("User {} authenticated successfully", username);
                return user;
            } else {
                log.warn("Failed authentication attempt for user {}: invalid password", username);
                throw new BadCredentialsException("Invalid password");
            }
        } else {
            log.warn("Failed authentication attempt for username {}: user not found", username);
            throw new BadCredentialsException("Username not found");
        }
    }
}
