package com.example.studentsystem.web.controller;

import com.example.studentsystem.security.JwtUtil;
import com.example.studentsystem.entity.User;
import com.example.studentsystem.service.UserService;
import com.example.studentsystem.web.dto.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostMapping("/login")
    @Operation(summary = "管理员登录（JWT + BCrypt，兼容明文密码自动升级）")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest request) {
        User user = userService.findByUsername(request.getUsername());
        if (user == null) {
            return ResponseEntity.status(401).body("用户名或密码错误");
        }
        boolean matched = false;
        // 优先 BCrypt 匹配
        try {
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                matched = true;
            }
        } catch (Exception ignored) {}
        // 降级：明文匹配（自动升级为 BCrypt）
        if (!matched && request.getPassword().equals(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userService.updateById(user);
            matched = true;
        }
        if (matched) {
            String token = jwtUtil.generateToken(user.getUsername(), Map.of("role", user.getRole()));
            Map<String, Object> resp = new HashMap<>();
            resp.put("token", token);
            resp.put("username", user.getUsername());
            resp.put("role", user.getRole());
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.status(401).body("用户名或密码错误");
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String authHeader) {
        try {
            String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
            User user = userService.findByUsername(username);
            if (user != null) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("username", user.getUsername());
                resp.put("role", user.getRole());
                return ResponseEntity.ok(resp);
            }
        } catch (Exception ignored) {}
        return ResponseEntity.status(401).body("未授权");
    }
}
