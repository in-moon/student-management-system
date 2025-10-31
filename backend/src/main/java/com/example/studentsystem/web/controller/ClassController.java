package com.example.studentsystem.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsystem.entity.ClassEntity;
import com.example.studentsystem.service.ClassService;
import com.example.studentsystem.web.dto.ClassQuery;
import com.example.studentsystem.web.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@Tag(name = "Class", description = "班级管理接口")
public class ClassController {
    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @GetMapping
    @Operation(summary = "分页搜索班级")
    public PageResponse<ClassEntity> list(ClassQuery query) {
        Page<ClassEntity> page = classService.pageClasses(query);
        return new PageResponse<>(page.getTotal(), page.getRecords());
    }

    @GetMapping("/{id}")
    @Operation(summary = "班级详情")
    public ResponseEntity<ClassEntity> detail(@PathVariable("id") Long id) {
        ClassEntity c = classService.getById(id);
        return c != null ? ResponseEntity.ok(c) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "新增班级")
    public ResponseEntity<ClassEntity> create(@Valid @RequestBody ClassEntity c) {
        c.setId(null);
        classService.save(c);
        return ResponseEntity.ok(c);
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑班级")
    public ResponseEntity<ClassEntity> update(@PathVariable("id") Long id, @Valid @RequestBody ClassEntity c) {
        c.setId(id);
        boolean ok = classService.updateById(c);
        return ok ? ResponseEntity.ok(c) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除班级")
    public ResponseEntity<Void> remove(@PathVariable("id") Long id) {
        return classService.removeById(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping
    @Operation(summary = "批量删除班级")
    public ResponseEntity<Void> removeBatch(@RequestBody List<Long> ids) {
        classService.removeBatchByIds(ids);
        return ResponseEntity.noContent().build();
    }
}


