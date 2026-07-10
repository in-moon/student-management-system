package com.example.studentsystem.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsystem.entity.Enrollment;
import com.example.studentsystem.service.EnrollmentService;
import com.example.studentsystem.web.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@Tag(name = "Enrollment", description = "选课管理接口")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    @Operation(summary = "分页查询选课记录")
    public PageResponse<Enrollment> list(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) wrapper.eq(Enrollment::getStudentId, studentId);
        if (courseId != null) wrapper.eq(Enrollment::getCourseId, courseId);
        wrapper.orderByDesc(Enrollment::getEnrollDate);

        Page<Enrollment> result = enrollmentService.page(new Page<>(page, pageSize), wrapper);
        return new PageResponse<>(result.getTotal(), result.getRecords());
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "查询某学生的选课列表")
    public List<Enrollment> byStudent(@PathVariable Long studentId) {
        return enrollmentService.lambdaQuery()
                .eq(Enrollment::getStudentId, studentId)
                .orderByDesc(Enrollment::getEnrollDate)
                .list();
    }

    @PostMapping
    @Operation(summary = "学生选课")
    public ResponseEntity<?> enroll(@RequestBody Enrollment enrollment) {
        // 检查是否已选
        long count = enrollmentService.lambdaQuery()
                .eq(Enrollment::getStudentId, enrollment.getStudentId())
                .eq(Enrollment::getCourseId, enrollment.getCourseId())
                .count();
        if (count > 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "该学生已选此课程"));
        }
        enrollment.setEnrollDate(LocalDate.now());
        enrollment.setStatus("ENROLLED");
        enrollmentService.save(enrollment);
        return ResponseEntity.ok(enrollment);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新选课信息（如录入成绩）")
    public ResponseEntity<Enrollment> update(@PathVariable Long id, @RequestBody Enrollment enrollment) {
        enrollment.setId(id);
        enrollmentService.updateById(enrollment);
        return ResponseEntity.ok(enrollment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "退课")
    public ResponseEntity<?> drop(@PathVariable Long id) {
        enrollmentService.removeById(id);
        return ResponseEntity.ok().build();
    }
}
