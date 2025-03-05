package com.example.GreetingApp.service;

import com.example.GreetingApp.dto.LoginDTO;
import com.example.GreetingApp.dto.UserDto;
import com.example.GreetingApp.dto.LoginResponseDto;  // Import the LoginResponseDto
import com.example.GreetingApp.model.AuthUser;
import com.example.GreetingApp.security.JwtUtil;
import com.example.GreetingApp.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthUserService {

    private final AuthUserRepository authUserRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;  // ✅ Injecting PasswordEncoder
    private final UserDetailsService userDetailsService; // ✅ Inject UserDetailsService for validation

    public String registerUser(UserDto userDTO) {
        if (authUserRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            return "Email is already in use.";
        }

        AuthUser user = AuthUser.builder()
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword())) // ✅ Using injected passwordEncoder
                .build();

        authUserRepository.save(user);

        emailService.sendEmail(user.getEmail(), "Welcome!", "Your registration is successful.");

        return "User registered successfully!";
    }

    public LoginResponseDto loginUser(LoginDTO loginDTO) {
        Optional<AuthUser> userOptional = authUserRepository.findByEmail(loginDTO.getEmail());

        if (userOptional.isEmpty()) {
            return new LoginResponseDto("User not found!", null);
        }

        AuthUser user = userOptional.get();
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {  // ✅ Using injected passwordEncoder
            return new LoginResponseDto("Invalid email or password!", null);
        }

        String token = jwtUtil.generateToken(user.getEmail());

        // Return the token and a success message in LoginResponseDto format
        return new LoginResponseDto("Login successful!", token);
    }

    public boolean validateToken(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            if (username == null) return false;

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return jwtUtil.validateToken(token, userDetails);
        } catch (Exception e) {
            return false;
        }
    }
}
