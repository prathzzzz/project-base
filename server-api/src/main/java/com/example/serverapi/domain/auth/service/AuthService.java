package com.example.serverapi.domain.auth.service;

import com.example.serverapi.common.exception.ResourceNotFoundException;
import com.example.serverapi.common.exception.UnauthorizedException;
import com.example.serverapi.config.security.JwtUtil;
import com.example.serverapi.domain.auth.dto.AuthResponse;
import com.example.serverapi.domain.auth.dto.LoginRequest;
import com.example.serverapi.domain.auth.dto.RegisterRequest;
import com.example.serverapi.domain.auth.dto.UserDTO;
import com.example.serverapi.domain.auth.entity.Role;
import com.example.serverapi.domain.auth.entity.User;
import com.example.serverapi.domain.auth.mapper.UserMapper;
import com.example.serverapi.domain.auth.repository.RoleRepository;
import com.example.serverapi.domain.auth.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Value("${jwt.cookie-name}")
    private String cookieName;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletResponse response) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Get default USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Default USER role not found"));

        // Create new user
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .isActive(true)
                .roles(roles)
                .build();

        user = userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        // Set JWT in cookie
        setJwtCookie(response, token);

        // Map user to DTO
        UserDTO userDTO = userMapper.toDTO(user);

        return AuthResponse.builder()
                .message("User registered successfully")
                .user(userDTO)
                .build();
    }

    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        if (!authentication.isAuthenticated()) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // Get user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user is active
        if (!user.getIsActive()) {
            throw new UnauthorizedException("User account is deactivated");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        // Set JWT in cookie
        setJwtCookie(response, token);

        // Map user to DTO
        UserDTO userDTO = userMapper.toDTO(user);

        return AuthResponse.builder()
                .message("Login successful")
                .user(userDTO)
                .build();
    }

    public UserDTO getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getIsActive()) {
            throw new UnauthorizedException("User account is deactivated");
        }

        return userMapper.toDTO(user);
    }

    public void logout(HttpServletResponse response) {
        // Clear JWT cookie
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private void setJwtCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(expiration.intValue() / 1000); // Convert to seconds
        response.addCookie(cookie);
    }
}
