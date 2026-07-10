package com.example.studentsystem.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsystem.entity.Course;
import com.example.studentsystem.service.CourseService;
import com.example.studentsystem.web.dto.CourseQuery;
import com.example.studentsystem.web.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Course", description = "课程管理接口")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    @Operation(summary = "分页查询课程列表")
    public PageResponse<Course> list(CourseQuery query) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        if (query.getName() != null && !query.getName().isEmpty()) {
            wrapper.like(Course::getName, query.getName());
        }
        if (query.getTeacher() != null && !query.getTeacher().isEmpty()) {
            wrapper.like(Course::getTeacher, query.getTeacher());
        }
        if (query.getSemester() != null && !query.getSemester().isEmpty()) {
            wrapper.eq(Course::getSemester, query.getSemester());
        }
        wrapper.orderByAsc(Course::getCourseNo);

        Page<Course> page = courseService.page(
                new Page<>(query.getPage(), query.getPageSize()), wrapper);
        return new PageResponse<>(page.getTotal(), page.getRecords());
    }

    @GetMapping("/all")
    @Operation(summary = "获取全部课程（不分页）")
    public List<Course> all() {
        return courseService.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取课程详情")
    public ResponseEntity<Course> getById(@PathVariable Long id) {
        Course course = courseService.getById(id);
        return course != null ? ResponseEntity.ok(course) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "新增课程")
    public Course create(@RequestBody Course course) {
        courseService.save(course);
        return course;
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新课程")
    public ResponseEntity<Course> update(@PathVariable Long id, @RequestBody Course course) {
        course.setId(id);
        courseService.updateById(course);
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        courseService.removeById(id);
        return ResponseEntity.ok().build();
    }
}
