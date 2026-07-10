package com.example.studentsystem.web.dto;

import lombok.Data;

@Data
public class CourseQuery {
    private String name;
    private String teacher;
    private String semester;
    private Integer page = 1;
    private Integer pageSize = 20;
}
