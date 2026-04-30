package com.ai_ecommerce.ecommerce.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ai_ecommerce.ecommerce.model.User;
import com.ai_ecommerce.ecommerce.service.JwtUtil;
import com.ai_ecommerce.ecommerce.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // SIGNUP
    @PostMapping("/signup")
    public String signup(@RequestBody User user) {
        userService.register(user);
        return "User Registered Successfully";
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        try {
            User existingUser = userService.login(
                    user.getEmail(),
                    user.getPassword()
            );

            String token = jwtUtil.generateToken(existingUser.getEmail());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "name", existingUser.getName(),
                    "email", existingUser.getEmail()
            ));

        } catch (RuntimeException e) {

            if (e.getMessage().equals("INVALID_EMAIL")) {
                return ResponseEntity.status(401).body("Invalid Email ❌");
            }

            if (e.getMessage().equals("INVALID_PASSWORD")) {
                return ResponseEntity.status(401).body("Invalid Password ❌");
            }

            return ResponseEntity.status(500).body("Something went wrong");
        }
    }
}