package com.example.studentsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentsystem.entity.AiConfigEntity;

public interface AiConfigService extends IService<AiConfigEntity> {
    AiConfigEntity getByUserId(Long userId);
    void saveOrUpdateConfig(Long userId, String apiKey, String baseUrl, String model);
}
