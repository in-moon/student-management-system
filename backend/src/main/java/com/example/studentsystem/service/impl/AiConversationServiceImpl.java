package com.example.studentsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentsystem.entity.AiConversation;
import com.example.studentsystem.mapper.AiConversationMapper;
import com.example.studentsystem.service.AiConversationService;
import org.springframework.stereotype.Service;

@Service
public class AiConversationServiceImpl extends ServiceImpl<AiConversationMapper, AiConversation> implements AiConversationService {
}
