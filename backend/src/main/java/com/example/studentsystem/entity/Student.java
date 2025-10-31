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
    private Long id; // 数据库自增主键

    private String studentNo; // 学号
    private String name;      // 姓名
    private String gender;    // 性别
    private Integer age;      // 年龄
    private String clazz;     // 班级
    private String major;     // 专业
    private LocalDate enrollDate; // 入学日期
    private String phone;     // 联系方式
}


