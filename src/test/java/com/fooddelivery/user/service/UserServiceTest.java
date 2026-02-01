package com.fooddelivery.user.service;

import com.fooddelivery.common.enums.Role;
import com.fooddelivery.user.dto.RegisterRequest;
import com.fooddelivery.user.dto.UserResponse;
import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    private RegisterRequest registerRequest;
    private User user;
    
    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        registerRequest.setPhone("1234567890");
        registerRequest.setRole(Role.CUSTOMER);
        
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .phone("1234567890")
                .role(Role.CUSTOMER)
                .active(true)
                .build();
    }
    
    @Test
    void testRegister_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        UserResponse response = userService.register(registerRequest);
        
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test", response.getFirstName());
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void testRegister_EmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        
        assertThrows(IllegalArgumentException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testGetById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        UserResponse response = userService.getById(1L);
        
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("test@example.com", response.getEmail());
    }
    
    @Test
    void testGetById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(com.fooddelivery.common.exception.ResourceNotFoundException.class, 
                () -> userService.getById(1L));
    }
}
