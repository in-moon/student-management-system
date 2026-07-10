package com.example.studentsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("course")
public class Course implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String courseNo;
    private String name;
    private BigDecimal credit;
    private String teacher;
    private String semester;
    private Integer maxStudents;
    private String description;
    private LocalDateTime createdAt;
}
