package com.app.dto;

import com.app.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String username;
    private Role role;

    // Getters and Setters
}