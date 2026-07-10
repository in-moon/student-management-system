-- ============================================
-- 学生管理系统 - 数据库初始化脚本
-- 数据库：MySQL 8.0+
-- 包含：E-R 模型 → 关系模型 完整设计
-- ============================================

-- 用户表（认证鉴权）
DROP TABLE IF EXISTS `ai_conversation`;
DROP TABLE IF EXISTS `enrollment`;
DROP TABLE IF EXISTS `course`;
DROP TABLE IF EXISTS `student`;
DROP TABLE IF EXISTS `class`;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(64) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT 'BCrypt加密密码',
  `role` VARCHAR(32) DEFAULT 'ADMIN' COMMENT '角色：ADMIN/TEACHER',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 默认管理员（密码: admin123，BCrypt加密）
INSERT INTO `user` (`username`, `password`, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'ADMIN');

-- 班级表
CREATE TABLE `class` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '班级ID',
  `class_no` VARCHAR(64) NOT NULL COMMENT '班级编号',
  `name` VARCHAR(100) NOT NULL COMMENT '班级名称',
  `grade` VARCHAR(20) DEFAULT NULL COMMENT '年级',
  `major` VARCHAR(100) DEFAULT NULL COMMENT '专业',
  `counselor` VARCHAR(100) DEFAULT NULL COMMENT '辅导员',
  `student_count` INT DEFAULT 0 COMMENT '学生人数',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_no` (`class_no`),
  INDEX `idx_class_major` (`major`),
  INDEX `idx_class_grade` (`grade`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

INSERT INTO `class` (`class_no`, `name`, `grade`, `major`, `counselor`, `student_count`) VALUES
('JSJ-2301', '计科2301班', '2023', '计算机科学与技术', '王老师', 2),
('RGC-2302', '软工2302班', '2023', '软件工程', '李老师', 0),
('DSJ-2301', '大数据2301班', '2023', '数据科学与大数据技术', '张老师', 0),
('AI-2301', '人工智能2301班', '2023', '人工智能', '刘老师', 0);

-- 学生表（含复合索引优化查询）
CREATE TABLE `student` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `student_no` VARCHAR(64) NOT NULL COMMENT '学号',
  `name` VARCHAR(100) NOT NULL COMMENT '姓名',
  `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别',
  `age` INT DEFAULT NULL COMMENT '年龄',
  `clazz` VARCHAR(100) DEFAULT NULL COMMENT '班级名称（显示用）',
  `class_id` BIGINT DEFAULT NULL COMMENT '班级ID（外键）',
  `major` VARCHAR(100) DEFAULT NULL COMMENT '专业',
  `enroll_date` DATE DEFAULT NULL COMMENT '入学日期',
  `phone` VARCHAR(50) DEFAULT NULL COMMENT '电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '地址',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_no` (`student_no`),
  INDEX `idx_student_name` (`name`),
  INDEX `idx_student_major` (`major`),
  INDEX `idx_student_class_id` (`class_id`),
  INDEX `idx_student_enroll_date` (`enroll_date`),
  INDEX `idx_student_name_major` (`name`, `major`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

INSERT INTO `student` (`student_no`, `name`, `gender`, `age`, `clazz`, `class_id`, `major`, `enroll_date`, `phone`, `email`) VALUES
('20230001', '张三', '男', 20, '计科2301班', 1, '计算机科学与技术', '2023-09-01', '13800000001', 'zhangsan@stu.edu.cn'),
('20230002', '李四', '女', 19, '计科2301班', 1, '计算机科学与技术', '2023-09-01', '13800000002', 'lisi@stu.edu.cn'),
('20230003', '王五', '男', 21, '软工2302班', 2, '软件工程', '2023-09-01', '13800000003', 'wangwu@stu.edu.cn'),
('20230004', '赵六', '女', 20, '大数据2301班', 3, '数据科学与大数据技术', '2023-09-01', '13800000004', 'zhaoliu@stu.edu.cn');

-- 课程表
CREATE TABLE `course` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_no` VARCHAR(64) NOT NULL COMMENT '课程编号',
  `name` VARCHAR(200) NOT NULL COMMENT '课程名称',
  `credit` DECIMAL(3,1) DEFAULT 0.0 COMMENT '学分',
  `teacher` VARCHAR(100) DEFAULT NULL COMMENT '授课教师',
  `semester` VARCHAR(50) DEFAULT NULL COMMENT '学期',
  `max_students` INT DEFAULT 60 COMMENT '最大选课人数',
  `description` TEXT DEFAULT NULL COMMENT '课程描述',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_no` (`course_no`),
  INDEX `idx_course_name` (`name`),
  INDEX `idx_course_teacher` (`teacher`),
  INDEX `idx_course_semester` (`semester`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

INSERT INTO `course` (`course_no`, `name`, `credit`, `teacher`, `semester`, `max_students`, `description`) VALUES
('CS101', '数据结构与算法', 4.0, '陈教授', '2025-2026-1', 80, '线性表、树、图、排序、查找等核心数据结构'),
('CS102', '数据库原理与应用', 3.5, '刘教授', '2025-2026-1', 60, '关系数据库理论、SQL语言、数据库设计范式'),
('CS103', '最优化理论及应用', 3.0, '赵教授', '2025-2026-1', 50, '线性规划、非线性规划、智能优化算法'),
('CS201', '软件工程', 3.0, '王教授', '2025-2026-2', 70, '软件开发流程、UML建模、敏捷开发方法'),
('CS202', '人工智能导论', 3.5, '李教授', '2025-2026-2', 60, '机器学习、深度学习、自然语言处理基础'),
('CS203', '计算机网络', 3.5, '周教授', '2025-2026-2', 80, 'TCP/IP协议栈、网络拓扑、网络安全');

-- 选课表（学生-课程 多对多关系）
CREATE TABLE `enrollment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '选课记录ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `enroll_date` DATE DEFAULT NULL COMMENT '选课日期',
  `score` DECIMAL(5,2) DEFAULT NULL COMMENT '成绩',
  `status` VARCHAR(20) DEFAULT 'ENROLLED' COMMENT '状态：ENROLLED/COMPLETED/DROPPED',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_course` (`student_id`, `course_id`),
  INDEX `idx_enrollment_student` (`student_id`),
  INDEX `idx_enrollment_course` (`course_id`),
  INDEX `idx_enrollment_score` (`score`),
  INDEX `idx_enrollment_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选课表';

INSERT INTO `enrollment` (`student_id`, `course_id`, `enroll_date`, `score`, `status`) VALUES
(1, 1, '2025-09-01', 85.5, 'COMPLETED'),
(1, 2, '2025-09-01', 92.0, 'COMPLETED'),
(1, 3, '2025-09-01', 78.5, 'ENROLLED'),
(2, 1, '2025-09-01', 90.0, 'COMPLETED'),
(2, 2, '2025-09-01', 88.5, 'ENROLLED');

-- AI对话历史表
CREATE TABLE `ai_conversation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '对话记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role` VARCHAR(20) NOT NULL COMMENT '角色：user/assistant/system',
  `content` TEXT NOT NULL COMMENT '对话内容',
  `model` VARCHAR(100) DEFAULT NULL COMMENT '使用的AI模型',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_conv_user` (`user_id`),
  INDEX `idx_conv_created` (`created_at`),
  INDEX `idx_conv_user_created` (`user_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话历史表';
