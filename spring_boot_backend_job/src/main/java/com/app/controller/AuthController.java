package com.app.controller;

import com.app.dto.*;
import com.app.entities.User;
import com.app.service.AuthService;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
@XSlf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO user) {
        try {
            User registeredUser = authService.registerUser(user);
            UserResponse u=new UserResponse();
            u.setUsername(registeredUser.getUsername());
            u.setRole(registeredUser.getRole());
            return new ResponseEntity<>(u, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Login a user and generate JWT token
//    @PostMapping("/login")
//    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
//        try {
//            User user = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
//            // Use the role from the authenticated user
//            String token = authService.generateToken(user);
//            return new ResponseEntity<>(token, HttpStatus.OK);
//        } catch (BadCredentialsException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
//        }
//    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            User user = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            String token = String.valueOf(authService.generateToken(user));  // ✅ Pass response if required

            // ✅ Store JWT in HTTP-only Cookie
            Cookie jwtCookie = new Cookie("jwt_token", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(60 * 60 * 24);

            response.addCookie(jwtCookie); // ✅ Send cookie in response

            return new ResponseEntity<>(token, HttpStatus.OK);
//            return ResponseEntity.ok("Login successful. JWT stored in HTTP-only cookie.");
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.sendOtpForReset(request);
        return new ResponseEntity<>("OTP sent successfully", HttpStatus.OK);
    }

    // Verify OTP and reset password
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody VerifyOTPRequest request) {
        authService.verifyOtpAndResetPassword(request);
        return new ResponseEntity<>("Password reset successful", HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
            return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid Authorization header", HttpStatus.BAD_REQUEST);
    }
}
