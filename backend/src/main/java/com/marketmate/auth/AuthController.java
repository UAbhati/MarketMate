package com.marketmate.auth;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/")
    public void redirectToLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/auth/login");
    }

    @GetMapping("/chat")
    public String home() {
        return "Welcome! You are logged in.";
    }


    @PostMapping("/register")
    public String register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestParam String email,
            @RequestParam String password,
            HttpServletResponse response) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                String token = jwtUtil.generateToken(email);
                return Map.of("token", token);
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return Map.of("error", "Invalid credentials");
    }

    @GetMapping("/login")
    public String loginRedirect() {
        return "Please log in at /auth/login using POST.";
    }
}
