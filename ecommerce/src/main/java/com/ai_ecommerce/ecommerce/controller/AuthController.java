package com.ai_ecommerce.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public String login(@RequestBody User user) {

        User existingUser = userService.login(
                user.getEmail(),
                user.getPassword()
        );

        return jwtUtil.generateToken(existingUser.getEmail());
    }
}