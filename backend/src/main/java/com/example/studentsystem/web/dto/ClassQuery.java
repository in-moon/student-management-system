package com.example.studentsystem.web.dto;

import lombok.Data;

@Data
public class ClassQuery {
    private String keyword; // name/classNo/major 模糊
    private String grade;
    private String sortBy;    // name/classNo/grade
    private String sortOrder; // asc/desc
    private long pageNum = 1;
    private long pageSize = 10;
}


