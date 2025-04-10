package com.app.service;


import com.app.entities.CustomOAuth2User;
import com.app.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private AuthService authService; // To generate JWT token

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // Create or fetch user from DB
        User user = authService.registerOrFetchOAuthUser(oAuth2User);

        // Generate JWT token
        String jwtToken = authService.generateToken(user);



        Cookie jwtCookie = new Cookie("jwt_token", jwtToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true); // Set to true in production (HTTPS required)
        jwtCookie.setPath("/"); // Cookie accessible across the app
        jwtCookie.setMaxAge(60 * 60 * 24); // 1 day expiration
        response.addCookie(jwtCookie);

        // Redirect to frontend (WITHOUT token in URL)
        response.sendRedirect("http://localhost:5173/homepage");
        // Send JSON response with token
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"token\": \"" + jwtToken + "\"}");

        //http://localhost:8081/oauth2/authorization/google
        // Redirect with token
//        response.sendRedirect("http://your-frontend.com/oauth2/success?token=" + token);
    }
}


