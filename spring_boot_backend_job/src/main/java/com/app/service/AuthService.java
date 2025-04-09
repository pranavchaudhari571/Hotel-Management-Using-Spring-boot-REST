package com.app.service;

import com.app.dao.UserRepository;
import com.app.dto.ForgotPasswordRequest;
import com.app.dto.UserDTO;
import com.app.dto.VerifyOTPRequest;
import com.app.entities.CustomOAuth2User;
import com.app.entities.Role;
import com.app.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AuthService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Key secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode("ZGpGVHVtMkZ3c3Y4TmJqU2RSbVJIV1lLcndITjVXSjdTY0FGVTF4UUV3TExIVm5WdUJuT0xZR3U="));

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String TOKEN_PREFIX = "TOKEN:";
    private static final String OTP_PREFIX = "OTP:";
    private static final int OTP_EXPIRATION_MINUTES = 5;
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


    @Async("asyncTaskExecutor")
    public void sendOtpForReset(ForgotPasswordRequest request) {
        User user = userRepository.findByUsername(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String otp = String.valueOf(100000 + new Random().nextInt(900000)); // Generate 6-digit OTP
        redisTemplate.opsForValue().set(OTP_PREFIX + user.getUsername(), otp, OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES);

        // Send Fancy HTML Email
        String subject = "🔐 Reset Your Password - OTP Inside!";
        String body = generateOtpEmailTemplate(user.getUsername(), otp);

        try {
            notificationService.sendHtmlEmail(user.getUsername(), subject, body);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}", user.getUsername(), e);
            throw new RuntimeException("Error sending OTP email.");
        }
    }

    public void verifyOtpAndResetPassword(VerifyOTPRequest request) {
        String storedOtp = (String) redisTemplate.opsForValue().get(OTP_PREFIX + request.getEmail());

        if (storedOtp == null || !storedOtp.equals(request.getOtp())) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        User user = userRepository.findByUsername(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        redisTemplate.delete(OTP_PREFIX + request.getEmail()); // Remove OTP after successful reset
    }


    @Async("asyncTaskExecutor")
    private String generateOtpEmailTemplate(String username, String otp) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }
                .container { max-width: 500px; margin: auto; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }
                h2 { color: #333; text-align: center; }
                .otp { font-size: 24px; font-weight: bold; color: #ff6600; text-align: center; padding: 10px; background: #eee; border-radius: 5px; margin: 20px 0; }
                .footer { text-align: center; font-size: 12px; color: #666; margin-top: 20px; }
                .button { display: inline-block; padding: 10px 20px; background: #28a745; color: white; text-decoration: none; border-radius: 5px; margin-top: 10px; }
            </style>
        </head>
        <body>
            <div class="container">
                <h2>Reset Your Password</h2>
                <p>Hi %s,</p>
                <p>You requested a password reset. Use the OTP below to proceed:</p>
                <div class="otp">%s</div>
                <p>This OTP is valid for only <strong>5 minutes</strong>. Do not share it with anyone.</p>
                <p>If you did not request this, please ignore this email.</p>
                <div style="text-align: center;">
                    <a href="#" class="button">Reset Password</a>
                </div>
                <div class="footer">
                    <p>🔐 Secure Login System | &copy; 2025 YourApp</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(username, otp);
    }
}
