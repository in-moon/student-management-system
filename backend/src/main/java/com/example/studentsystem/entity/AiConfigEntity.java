package com.example.studentsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("ai_config")
public class AiConfigEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String apiKey;
    private String baseUrl;
    private String model;
    private LocalDateTime updatedAt;
}
