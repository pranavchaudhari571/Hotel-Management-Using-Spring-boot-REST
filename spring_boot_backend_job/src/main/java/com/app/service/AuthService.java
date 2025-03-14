package com.app.service;

import com.app.dao.UserRepository;
import com.app.dto.UserDTO;
import com.app.entities.CustomOAuth2User;
import com.app.entities.Role;
import com.app.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AuthService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Key secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode("ZGpGVHVtMkZ3c3Y4TmJqU2RSbVJIV1lLcndITjVXSjdTY0FGVTF4UUV3TExIVm5WdUJuT0xZR3U="));

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String TOKEN_PREFIX = "TOKEN:";
    // Generate JWT Token with username, role(s), and user ID (adminId)
    public String generateToken(User user) {
        long expirationTime = 10000 * 60 * 60 * 10; // 10 hours

        String token= Jwts.builder()
                .setSubject(user.getUsername())  // Set username as subject
                .claim("role", user.getRole().name())  // Role as claim (single role)
                .claim("adminId", user.getId())  // Add user ID as a claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))  // Set expiration time (10 hours)
                .signWith(secretKey)  // Sign with secret key
                .compact();
        String tokenKey = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(tokenKey, true, expirationTime, TimeUnit.MILLISECONDS);

        return token;
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


    public void logout(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        redisTemplate.delete(tokenKey);  // Blacklist the token
    }

    public boolean isTokenValid(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.opsForValue().get(tokenKey));
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

    public User registerOrFetchOAuthUser(CustomOAuth2User oAuth2User) {
        String email = oAuth2User.getEmail(); // Extract email from OAuth2User

        // Check if user already exists
        Optional<User> existingUser = userRepository.findByUsername(email);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        // If user does not exist, create a new one
        User newUser = new User();
        newUser.setUsername(email);
        newUser.setPassword(""); // No password required for OAuth users
        newUser.setRole(Role.USER); // Default role

        // Save to database
        return userRepository.save(newUser);
    }
}
