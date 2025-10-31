package com.example.studentsystem.web.controller;

import com.example.studentsystem.security.JwtUtil;
import com.example.studentsystem.entity.User;
import com.example.studentsystem.service.UserService;
import com.example.studentsystem.web.dto.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "认证接口")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "管理员登录（JWT）")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest request) {
        User user = userService.findByUsername(request.getUsername());
        if (user != null && user.getPassword().equals(request.getPassword())) {
            String token = jwtUtil.generateToken(user.getUsername(), Map.of("role", user.getRole()));
            Map<String, Object> resp = new HashMap<>();
            resp.put("token", token);
            resp.put("username", user.getUsername());
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.status(401).body("用户名或密码错误");
    }
}


