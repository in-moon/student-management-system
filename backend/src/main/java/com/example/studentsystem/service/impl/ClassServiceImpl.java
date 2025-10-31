package com.example.studentsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentsystem.entity.ClassEntity;
import com.example.studentsystem.mapper.ClassMapper;
import com.example.studentsystem.service.ClassService;
import com.example.studentsystem.web.dto.ClassQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ClassServiceImpl extends ServiceImpl<ClassMapper, ClassEntity> implements ClassService {
    @Override
    public Page<ClassEntity> pageClasses(ClassQuery query) {
        Page<ClassEntity> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ClassEntity> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(ClassEntity::getName, query.getKeyword())
                   .or().like(ClassEntity::getClassNo, query.getKeyword())
                   .or().like(ClassEntity::getMajor, query.getKeyword());
        }
        if (StringUtils.hasText(query.getGrade())) {
            wrapper.eq(ClassEntity::getGrade, query.getGrade());
        }

        if (StringUtils.hasText(query.getSortBy())) {
            boolean asc = "asc".equalsIgnoreCase(query.getSortOrder());
            wrapper.orderBy(true, asc, switch (query.getSortBy()) {
                case "name" -> ClassEntity::getName;
                case "classNo" -> ClassEntity::getClassNo;
                case "grade" -> ClassEntity::getGrade;
                default -> ClassEntity::getId;
            });
        } else {
            wrapper.orderByDesc(ClassEntity::getId);
        }

        return this.page(page, wrapper);
    }
}


