package com.example.studentsystem.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsystem.entity.Student;
import com.example.studentsystem.service.StudentService;
import com.example.studentsystem.web.dto.PageResponse;
import com.example.studentsystem.web.dto.StudentQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Student", description = "学生管理接口")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    @Operation(summary = "分页搜索学生")
    public PageResponse<Student> list(StudentQuery query) {
        Page<Student> page = studentService.pageStudents(query);
        return new PageResponse<>(page.getTotal(), page.getRecords());
    }

    @GetMapping("/{id}")
    @Operation(summary = "学生详情")
    public ResponseEntity<Student> detail(@PathVariable("id") Long id) {
        Student student = studentService.getById(id);
        return student != null ? ResponseEntity.ok(student) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "新增学生")
    public ResponseEntity<Student> create(@Valid @RequestBody Student student) {
        student.setId(null);
        studentService.save(student);
        return ResponseEntity.ok(student);
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑学生")
    public ResponseEntity<Student> update(@PathVariable("id") Long id, @Valid @RequestBody Student student) {
        student.setId(id);
        boolean ok = studentService.updateById(student);
        return ok ? ResponseEntity.ok(student) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除学生")
    public ResponseEntity<Void> remove(@PathVariable("id") Long id) {
        return studentService.removeById(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping
    @Operation(summary = "批量删除学生")
    public ResponseEntity<Void> removeBatch(@RequestBody List<Long> ids) {
        studentService.removeBatchByIds(ids);
        return ResponseEntity.noContent().build();
    }
}


