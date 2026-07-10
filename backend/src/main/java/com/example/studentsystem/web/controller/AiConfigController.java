package com.example.studentsystem.web.controller;

import com.example.studentsystem.entity.AiConfigEntity;
import com.example.studentsystem.entity.User;
import com.example.studentsystem.security.JwtUtil;
import com.example.studentsystem.service.AiConfigService;
import com.example.studentsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai/config")
@Tag(name = "AI Config", description = "AI 用户配置接口")
public class AiConfigController {

    private final AiConfigService aiConfigService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AiConfigController(AiConfigService aiConfigService, JwtUtil jwtUtil, UserService userService) {
        this.aiConfigService = aiConfigService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "获取当前用户的 AI 配置")
    public Map<String, Object> getConfig(@RequestHeader("Authorization") String authHeader) {
        User user = getCurrentUser(authHeader);
        AiConfigEntity config = aiConfigService.getByUserId(user.getId());
        if (config != null) {
            // 返回时脱敏 apiKey
            String maskedKey = maskKey(config.getApiKey());
            return Map.of(
                "apiKey", maskedKey,
                "baseUrl", config.getBaseUrl() != null ? config.getBaseUrl() : "",
                "model", config.getModel() != null ? config.getModel() : "",
                "hasConfig", true
            );
        }
        return Map.of("apiKey", "", "baseUrl", "", "model", "", "hasConfig", false);
    }

    @PutMapping
    @Operation(summary = "保存或更新 AI 配置")
    public Map<String, Object> saveConfig(@RequestBody Map<String, String> body,
                                          @RequestHeader("Authorization") String authHeader) {
        User user = getCurrentUser(authHeader);
        String apiKey = body.getOrDefault("apiKey", "");
        String baseUrl = body.getOrDefault("baseUrl", "");
        String model = body.getOrDefault("model", "gpt-3.5-turbo");

        // 如果 apiKey 是脱敏的（包含 ****），说明用户没修改，保留原值
        if (apiKey.contains("****") && apiKey.length() < 20) {
            AiConfigEntity existing = aiConfigService.getByUserId(user.getId());
            if (existing != null && existing.getApiKey() != null) {
                apiKey = existing.getApiKey();
            }
        }

        aiConfigService.saveOrUpdateConfig(user.getId(), apiKey, baseUrl, model);
        return Map.of("success", true, "message", "AI 配置已保存");
    }

    @DeleteMapping
    @Operation(summary = "删除 AI 配置（恢复默认）")
    public Map<String, Object> deleteConfig(@RequestHeader("Authorization") String authHeader) {
        User user = getCurrentUser(authHeader);
        aiConfigService.lambdaUpdate()
                .eq(AiConfigEntity::getUserId, user.getId())
                .remove();
        return Map.of("success", true, "message", "已恢复默认配置");
    }

    private User getCurrentUser(String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
        return userService.findByUsername(username);
    }

    private String maskKey(String key) {
        if (key == null || key.length() <= 8) return key;
        return key.substring(0, 4) + "****" + key.substring(key.length() - 4);
    }
}
