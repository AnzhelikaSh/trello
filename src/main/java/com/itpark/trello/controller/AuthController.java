package com.itpark.trello.controller;

import com.itpark.trello.dto.AuthRequest;
import com.itpark.trello.dto.AuthResponse;
import com.itpark.trello.dto.CreateUserRequest;
import com.itpark.trello.dto.UserDto;
import com.itpark.trello.security.JwtService;
import com.itpark.trello.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);

        // Получаем UserDto по username
        UserDto user = userService.getUserByUsername(request.getUsername());

        return new AuthResponse(token, user);
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody CreateUserRequest request) {
        UserDto user = userService.createUser(request);

        // Автоматически логиним после регистрации
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, user);
    }
}
