package com.app.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

public class CustomUserDetails extends User {

    private Long adminId;  // This is your custom field to hold the adminId

    // Constructor: Superclass (User) only needs username and authorities, we don't need password
    public CustomUserDetails(String username, Collection<? extends GrantedAuthority> authorities, Long adminId) {
        super(username, "", authorities);  // Pass empty string for password (not needed in JWT-based auth)
        this.adminId = adminId;
    }

    public Long getAdminId() {
        return adminId;  // Getter for adminId
    }
}
