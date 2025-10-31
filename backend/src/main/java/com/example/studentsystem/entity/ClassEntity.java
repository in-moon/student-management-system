package com.example.studentsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("class")
public class ClassEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String classNo;    // 班级编号
    private String name;       // 班级名称
    private String grade;      // 年级
    private String major;      // 专业
    private String counselor;  // 辅导员
    private LocalDateTime createdAt;
}


