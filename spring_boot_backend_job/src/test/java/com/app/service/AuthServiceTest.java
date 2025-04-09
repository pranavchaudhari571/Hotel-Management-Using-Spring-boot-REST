package com.app.service;

import com.app.dao.UserRepository;
import com.app.dto.ForgotPasswordRequest;
import com.app.dto.UserDTO;
import com.app.entities.Role;
import com.app.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisTemplate<String,Object> redisTemplate;

    @BeforeEach
    public void setUp() throws Exception {

    }
    @Test
    public void testRegisterUser_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("testpassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(Role.USER);

        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result=authService.registerUser(userDTO);
        assertNotNull(result);
        assertEquals(result.getId(),1L);
        assertEquals(result.getUsername(),"testuser");
        assertEquals(result.getPassword(),"encodedPassword");
        assertEquals(result.getRole(),Role.USER);
        verify(userRepository).save(any(User.class));

    }
    @Test
    public void testRegisterUser_FailureUserToken() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("testpassword");

        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser(userDTO);
        });
//        User result=authService.registerUser(userDTO);
//        assertEquals(result,null);
    }

    @Test
    @Disabled
    public void testGenerateToken_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(Role.ADMIN);
        user.setId(1L);
        String token = authService.generateToken(user);
        assertNotNull(token);
        assertTrue(token.length() > 10);
    }

    @Test
    @Disabled
    void testSendOtpForReset() throws MessagingException {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("user@example.com");

        User user = new User();
        user.setUsername("user@example.com");

        when(userRepository.findByUsername("user@example.com")).thenReturn(Optional.of(user));

        // Act
        authService.sendOtpForReset(request);

        // Assert
        verify(redisTemplate).opsForValue().set(anyString(), anyString(), eq(5L), eq(TimeUnit.MINUTES));
        verify(notificationService).sendHtmlEmail(eq("pranavprem1613@gmail.com"), anyString(), anyString());
    }


}
