package com.example.studentsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("student")
public class Student {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String studentNo;
    private String name;
    private String gender;
    private Integer age;
    private String clazz;        // 班级名称（显示）
    private Long classId;        // 班级外键
    private String major;
    private LocalDate enrollDate;
    private String phone;
    private String email;
    private String address;
}
