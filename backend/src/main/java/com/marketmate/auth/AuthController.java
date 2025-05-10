package com.marketmate.auth;

import javax.servlet.http.HttpServletResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public static class LoginRequest {
        public String email;
        public String password;
    }

    @Operation(summary = "Register a new user")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return Map.of("message", "User registered successfully");
    }

    @Operation(summary = "Authenticate user and return JWT token")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @PostMapping("/login")
    public Map<String, String> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User login credentials")
            @RequestBody LoginRequest req,
            HttpServletResponse response) {
        Optional<User> userOpt = userRepository.findByEmail(req.email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(req.password, user.getPassword())) {
                String token = jwtUtil.generateToken(req.email);
                return Map.of("token", token);
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return Map.of("error", "Invalid credentials");
    }
}
