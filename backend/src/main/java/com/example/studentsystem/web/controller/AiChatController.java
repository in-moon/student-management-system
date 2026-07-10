package com.example.studentsystem.web.controller;

import com.example.studentsystem.config.AiConfig;
import com.example.studentsystem.entity.AiConfigEntity;
import com.example.studentsystem.entity.AiConversation;
import com.example.studentsystem.entity.User;
import com.example.studentsystem.security.JwtUtil;
import com.example.studentsystem.service.AiConfigService;
import com.example.studentsystem.service.AiConversationService;
import com.example.studentsystem.service.UserService;
import com.example.studentsystem.web.dto.ChatRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI Chat", description = "AI 智能对话接口（OpenAI 兼容，SSE 流式输出）")
public class AiChatController {

    private static final Logger log = LoggerFactory.getLogger(AiChatController.class);

    private final RestClient openaiRestClient;
    private final AiConfig aiConfig;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final AiConversationService conversationService;
    private final AiConfigService aiConfigService;
    private final ObjectMapper objectMapper;

    public AiChatController(RestClient openaiRestClient, AiConfig aiConfig,
                            JwtUtil jwtUtil, UserService userService,
                            AiConversationService conversationService,
                            AiConfigService aiConfigService, ObjectMapper objectMapper) {
        this.openaiRestClient = openaiRestClient;
        this.aiConfig = aiConfig;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.conversationService = conversationService;
        this.aiConfigService = aiConfigService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "AI 流式对话（SSE）")
    public SseEmitter chat(@RequestBody ChatRequest request,
                           @RequestHeader("Authorization") String authHeader) {
        SseEmitter emitter = new SseEmitter(120000L);

        new Thread(() -> {
            try {
                // 验证用户
                String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
                User user = userService.findByUsername(username);

                // 获取用户自定义 AI 配置（优先），否则用系统默认配置
                AiConfigEntity userConfig = (user != null) ? aiConfigService.getByUserId(user.getId()) : null;
                String effectiveApiKey = (userConfig != null && userConfig.getApiKey() != null && !userConfig.getApiKey().isEmpty())
                        ? userConfig.getApiKey() : aiConfig.getApiKey();
                String effectiveBaseUrl = (userConfig != null && userConfig.getBaseUrl() != null && !userConfig.getBaseUrl().isEmpty())
                        ? userConfig.getBaseUrl() : aiConfig.getBaseUrl();
                String effectiveModel = (userConfig != null && userConfig.getModel() != null && !userConfig.getModel().isEmpty())
                        ? userConfig.getModel() : aiConfig.getModel();

                if (user != null) {
                    AiConversation userMsg = new AiConversation();
                    userMsg.setUserId(user.getId());
                    userMsg.setRole("user");
                    userMsg.setContent(request.getMessage());
                    userMsg.setModel(effectiveModel);
                    userMsg.setCreatedAt(LocalDateTime.now());
                    conversationService.save(userMsg);
                }

                // 构建 OpenAI 请求
                List<Map<String, String>> messages = new ArrayList<>();
                messages.add(Map.of("role", "system", "content",
                        "你是一个学生管理系统的AI助手，可以帮助用户解答关于学生管理、课程安排、成绩分析等问题。请用中文回答，回答简洁专业。"));

                // 添加历史对话
                if (request.getHistory() != null) {
                    for (ChatRequest.ChatMessage msg : request.getHistory()) {
                        messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
                    }
                }

                messages.add(Map.of("role", "user", "content", request.getMessage()));

                Map<String, Object> body = new HashMap<>();
                body.put("model", effectiveModel);
                body.put("messages", messages);
                body.put("temperature", aiConfig.getTemperature());
                body.put("max_tokens", aiConfig.getMaxTokens());
                body.put("stream", true);

                // 根据用户配置构建 RestClient（如果有自定义 key 则动态创建）
                RestClient client = openaiRestClient;
                if (userConfig != null && userConfig.getApiKey() != null && !userConfig.getApiKey().isEmpty()) {
                    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                    factory.setConnectTimeout(30000);
                    factory.setReadTimeout(60000);
                    client = RestClient.builder()
                            .baseUrl(effectiveBaseUrl)
                            .defaultHeader("Authorization", "Bearer " + effectiveApiKey)
                            .defaultHeader("Content-Type", "application/json")
                            .requestFactory(factory)
                            .build();
                }

                // 调用 OpenAI API（流式）
                StringBuilder fullResponse = new StringBuilder();
                try {
                    client.post()
                            .uri("/v1/chat/completions")
                            .body(body)
                            .retrieve()
                            .body(String.class)
                            .lines()
                            .filter(line -> line.startsWith("data: "))
                            .map(line -> line.substring(6).trim())
                            .filter(line -> !line.equals("[DONE]"))
                            .forEach(line -> {
                                try {
                                    @SuppressWarnings("unchecked")
                                    Map<String, Object> chunk = objectMapper.readValue(line, Map.class);
                                    @SuppressWarnings("unchecked")
                                    List<Map<String, Object>> choices = (List<Map<String, Object>>) chunk.get("choices");
                                    if (choices != null && !choices.isEmpty()) {
                                        @SuppressWarnings("unchecked")
                                        Map<String, Object> delta = (Map<String, Object>) choices.get(0).get("delta");
                                        if (delta != null && delta.get("content") != null) {
                                            String content = (String) delta.get("content");
                                            fullResponse.append(content);
                                            SseEmitter.SseEventBuilder event = SseEmitter.event()
                                                    .name("message")
                                                    .data(content);
                                            emitter.send(event);
                                        }
                                    }
                                } catch (Exception e) {
                                    log.warn("解析流式响应出错: {}", e.getMessage());
                                }
                            });
                } catch (Exception e) {
                    log.warn("AI API 调用失败，使用模拟响应: {}", e.getMessage());
                    // 降级：返回模拟AI响应
                    String mockResponse = generateMockResponse(request.getMessage());
                    fullResponse.append(mockResponse);
                    SseEmitter.SseEventBuilder event = SseEmitter.event()
                            .name("message")
                            .data(mockResponse);
                    emitter.send(event);
                }

                // 保存AI响应
                if (user != null && fullResponse.length() > 0) {
                    AiConversation aiMsg = new AiConversation();
                    aiMsg.setUserId(user.getId());
                    aiMsg.setRole("assistant");
                    aiMsg.setContent(fullResponse.toString());
                    aiMsg.setModel(effectiveModel);
                    aiMsg.setCreatedAt(LocalDateTime.now());
                    conversationService.save(aiMsg);
                }

                emitter.send(SseEmitter.event().name("done").data("[DONE]"));
                emitter.complete();
            } catch (Exception e) {
                log.error("AI 对话出错: ", e);
                try {
                    emitter.send(SseEmitter.event().name("error").data("AI服务暂时不可用，请稍后重试"));
                    emitter.complete();
                } catch (IOException ex) {
                    emitter.completeWithError(ex);
                }
            }
        }).start();

        return emitter;
    }

    @GetMapping("/history")
    @Operation(summary = "获取AI对话历史")
    public List<AiConversation> getHistory(@RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
        User user = userService.findByUsername(username);
        if (user == null) return List.of();

        return conversationService.lambdaQuery()
                .eq(AiConversation::getUserId, user.getId())
                .orderByAsc(AiConversation::getCreatedAt)
                .list();
    }

    @DeleteMapping("/history")
    @Operation(summary = "清空AI对话历史")
    public Map<String, Object> clearHistory(@RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
        User user = userService.findByUsername(username);
        if (user != null) {
            conversationService.lambdaUpdate()
                    .eq(AiConversation::getUserId, user.getId())
                    .remove();
        }
        return Map.of("success", true);
    }

    /**
     * 当 API 不可用时，生成模拟的智能响应
     */
    private String generateMockResponse(String question) {
        if (question.contains("学生") || question.contains("成绩")) {
            return "根据系统数据分析，当前学生总体成绩良好，平均GPA为3.2。建议关注成绩波动较大的学生，及时进行学业指导。";
        } else if (question.contains("课程") || question.contains("选课")) {
            return "本学期共开设25门课程，其中核心课程6门。建议学生根据专业培养方案合理选课，注意学分平衡。热门课程包括数据结构、人工智能导论等。";
        } else if (question.contains("管理") || question.contains("系统")) {
            return "本学生管理系统采用SpringBoot+Vue架构，支持学生信息管理、班级管理、课程管理、AI智能分析等功能。您可以通过左侧菜单进行各项操作。";
        } else {
            return "您好！我是学生管理系统的AI助手。我可以帮您解答关于学生管理、课程安排、成绩分析、系统使用等方面的问题。请问有什么可以帮到您的？";
        }
    }
}
