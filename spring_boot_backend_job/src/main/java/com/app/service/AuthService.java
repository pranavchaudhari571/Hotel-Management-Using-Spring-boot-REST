package com.app.service;

import com.app.dao.UserRepository;
import com.app.dto.UserDTO;
import com.app.entities.Role;
import com.app.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Key SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode("ZGpGVHVtMkZ3c3Y4TmJqU2RSbVJIV1lLcndITjVXSjdTY0FGVTF4UUV3TExIVm5WdUJuT0xZR3U="));

    // Generate JWT Token with username, role(s), and user ID (adminId)
    public String generateToken(User user) {
        long expirationTime = 10000 * 60 * 60 * 10; // 10 hours

        return Jwts.builder()
                .setSubject(user.getUsername())  // Set username as subject
                .claim("role", user.getRole().name())  // Role as claim (single role)
                .claim("adminId", user.getId())  // Add user ID as a claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))  // Set expiration time (10 hours)
                .signWith(SECRET_KEY)  // Sign with secret key
                .compact();
    }

    // Register a user and assign role (if not already assigned)
    public User registerUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        // Create a new User entity
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // Assign the role if it's null
        if (userDTO.getRole() == null) {
            user.setRole(Role.USER);  // Default to USER role if none is provided
        } else {
            user.setRole(userDTO.getRole());
        }

        // Saving the user in the repository
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
