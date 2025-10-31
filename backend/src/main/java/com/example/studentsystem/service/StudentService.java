package com.example.studentsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentsystem.entity.Student;
import com.example.studentsystem.web.dto.StudentQuery;

public interface StudentService extends IService<Student> {
    Page<Student> pageStudents(StudentQuery query);
}


