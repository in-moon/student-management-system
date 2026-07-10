package com.example.studentsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentsystem.entity.Enrollment;
import com.example.studentsystem.mapper.EnrollmentMapper;
import com.example.studentsystem.service.EnrollmentService;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentServiceImpl extends ServiceImpl<EnrollmentMapper, Enrollment> implements EnrollmentService {
}
