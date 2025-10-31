package com.example.studentsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password; // 简化：明文存储（演示用，生产请使用哈希）
    private String role;     // 例如：ADMIN/USER
    private LocalDateTime createdAt;
}


