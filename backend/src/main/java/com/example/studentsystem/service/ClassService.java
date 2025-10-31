package com.example.studentsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentsystem.entity.ClassEntity;
import com.example.studentsystem.web.dto.ClassQuery;

public interface ClassService extends IService<ClassEntity> {
    Page<ClassEntity> pageClasses(ClassQuery query);
}


