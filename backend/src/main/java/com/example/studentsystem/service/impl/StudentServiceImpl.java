package com.example.studentsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentsystem.entity.Student;
import com.example.studentsystem.mapper.StudentMapper;
import com.example.studentsystem.service.StudentService;
import com.example.studentsystem.web.dto.StudentQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    @Override
    public Page<Student> pageStudents(StudentQuery query) {
        Page<Student> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Student::getName, query.getKeyword())
                   .or().like(Student::getStudentNo, query.getKeyword());
        }
        if (StringUtils.hasText(query.getGender())) {
            wrapper.eq(Student::getGender, query.getGender());
        }
        if (StringUtils.hasText(query.getMajor())) {
            wrapper.eq(Student::getMajor, query.getMajor());
        }

        if (StringUtils.hasText(query.getSortBy())) {
            boolean asc = "asc".equalsIgnoreCase(query.getSortOrder());
            wrapper.orderBy(true, asc, switch (query.getSortBy()) {
                case "name" -> Student::getName;
                case "studentNo" -> Student::getStudentNo;
                case "age" -> Student::getAge;
                case "enrollDate" -> Student::getEnrollDate;
                default -> Student::getId;
            });
        } else {
            wrapper.orderByDesc(Student::getId);
        }

        return this.page(page, wrapper);
    }
}


