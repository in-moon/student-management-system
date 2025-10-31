package com.example.studentsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentsystem.entity.User;

public interface UserService extends IService<User> {
    User findByUsername(String username);
}


