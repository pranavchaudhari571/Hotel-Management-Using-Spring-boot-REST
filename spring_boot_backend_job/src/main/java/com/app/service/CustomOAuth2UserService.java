package com.app.service;


import com.app.dao.UserRepository;
import com.app.entities.CustomOAuth2User;
import com.app.entities.OAuth2UserInfo;
import com.app.entities.Role;
import com.app.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Optional;
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService jwtUtil; // JWT service to generate token

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        // Extract Google user details
        String email = oauth2User.getAttribute("email");
        String googleId = oauth2User.getAttribute("sub"); // Get Google ID

        if (email == null || googleId == null) {
            throw new OAuth2AuthenticationException("Invalid OAuth2 user details");
        }

        // Check if user exists in database
        Optional<User> existingUser = userRepository.findByUsername(email);
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            // Update Google ID if not set
            if (user.getGoogleId() == null) {
                user.setGoogleId(googleId);
                userRepository.save(user);
            }
        } else {
            // Register new OAuth2 user
            user = new User();
            user.setUsername(email);
            user.setGoogleId(googleId); // Store Google ID
            user.setPassword("");  // No password for OAuth users
            user.setRole(Role.USER);
            user = userRepository.save(user);
        }

        // Generate JWT for authenticated user
        String jwtToken = jwtUtil.generateToken(user);

        // Return custom OAuth2User with JWT included
        return new CustomOAuth2User(oauth2User, jwtToken);
    }
}

