package com.app.Util;

import com.app.service.CustomOAuth2UserService;
import com.app.service.OAuth2SuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtRequestFilter jwtRequestFilter; // Inject the JwtRequestFilter


    @Autowired
    @Lazy
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    @Lazy
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // You can change the encoder type if needed
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // Disable CSRF protection for JWT
                .authorizeRequests()
                .antMatchers("/auth/register", "/auth/login","/auth/forgot-password","/auth/reset-password","/actuator/prometheus","/actuator").permitAll()  // Allow open access to registration/login
                .antMatchers("/hotel/reservations/**").authenticated()
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()  // Allow access to API docs
                .antMatchers("/hotel/rooms/**").hasAuthority("ROLE_ADMIN")  // Use hasAuthority instead of hasRole
                .anyRequest().authenticated()  // Secure all other endpoints
                .and()
                .oauth2Login()
                .userInfoEndpoint().userService(customOAuth2UserService)
                .and()
                .successHandler(oAuth2SuccessHandler) // Generate JWT after OAuth2 login
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Stateless session management
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter to intercept requests
    }

}