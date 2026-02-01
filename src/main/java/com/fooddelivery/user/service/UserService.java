package com.fooddelivery.user.service;

import com.fooddelivery.common.exception.ResourceNotFoundException;
import com.fooddelivery.user.dto.RegisterRequest;
import com.fooddelivery.user.dto.UserResponse;
import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        // ✅ roles initialized safely (no NullPointerException)
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(request.getRole())
                .roles(Set.of(request.getRole()))
                .address(request.getAddress())
                .city(request.getCity())
                .pincode(request.getPincode())
                .active(true)
                .build();

        user = userRepository.save(user);

        return mapToResponse(user);
    }

    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToResponse(user);
    }

    public UserResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(Long userId, RegisterRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getPincode() != null) user.setPincode(request.getPincode());

        user = userRepository.save(user);

        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole())
                .roles(user.getRoles())
                .active(user.getActive())
                .address(user.getAddress())
                .city(user.getCity())
                .pincode(user.getPincode())
                .build();
    }
}
