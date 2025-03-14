package com.app.entities;


import java.util.Map;

public class OAuth2UserInfo {
    private final Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getId() {
        return (String) attributes.get("sub");  // Google provides "sub" as the unique user ID
    }

    public String getName() {
        return (String) attributes.get("name");  // Full name of the user
    }

    public String getEmail() {
        return (String) attributes.get("email"); // Email of the user
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}


