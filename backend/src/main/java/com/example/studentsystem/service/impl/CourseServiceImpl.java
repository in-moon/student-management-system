package com.example.studentsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentsystem.entity.Course;
import com.example.studentsystem.mapper.CourseMapper;
import com.example.studentsystem.service.CourseService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "courses")
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Override
    @Cacheable(key = "'all'")
    public List<Course> list() {
        return super.list();
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean save(Course entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateById(Course entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }
}
