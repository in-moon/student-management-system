package com.example.studentsystem.web.dto;

import lombok.Data;

@Data
public class StudentQuery {
    private String keyword;
    private String gender;
    private String major;

    private String sortBy;   // name/studentNo/age/enrollDate
    private String sortOrder; // asc/desc

    private long pageNum = 1;
    private long pageSize = 10;
}


