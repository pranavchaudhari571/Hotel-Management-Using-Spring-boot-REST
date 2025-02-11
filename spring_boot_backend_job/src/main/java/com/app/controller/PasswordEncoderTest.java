package com.app.controller;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class PasswordEncoderTest {
    public static void main(String[] args) {
        // Initialize BCryptPasswordEncoder
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Your raw password (the one you want to store)
        String rawPassword = "2e21a712-b700-4a6e-9ab5-af8db74d48b6";  // Change this to the actual raw password you want to encode

        // Encode the password
        String encodedPassword = encoder.encode(rawPassword);

        // Print the encoded password to the console
        System.out.println("Encoded password: " + encodedPassword);
    }
}

