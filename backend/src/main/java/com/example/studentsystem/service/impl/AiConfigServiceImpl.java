package com.example.studentsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentsystem.entity.AiConfigEntity;
import com.example.studentsystem.mapper.AiConfigMapper;
import com.example.studentsystem.service.AiConfigService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AiConfigServiceImpl extends ServiceImpl<AiConfigMapper, AiConfigEntity> implements AiConfigService {

    @Override
    public AiConfigEntity getByUserId(Long userId) {
        return this.lambdaQuery()
                .eq(AiConfigEntity::getUserId, userId)
                .one();
    }

    @Override
    public void saveOrUpdateConfig(Long userId, String apiKey, String baseUrl, String model) {
        AiConfigEntity existing = getByUserId(userId);
        if (existing != null) {
            existing.setApiKey(apiKey);
            existing.setBaseUrl(baseUrl);
            existing.setModel(model);
            existing.setUpdatedAt(LocalDateTime.now());
            this.updateById(existing);
        } else {
            AiConfigEntity config = new AiConfigEntity();
            config.setUserId(userId);
            config.setApiKey(apiKey);
            config.setBaseUrl(baseUrl);
            config.setModel(model);
            config.setUpdatedAt(LocalDateTime.now());
            this.save(config);
        }
    }
}
