-- ============================================
-- 学生管理系统 - DM8 达梦数据库初始化脚本
-- 数据库：达梦 DM8（Oracle 兼容模式）
-- 与 MySQL 版的区别：VARCHAR2, IDENTITY, 无 DROP IF EXISTS
-- ============================================

-- 用户表
BEGIN EXECUTE IMMEDIATE 'DROP TABLE ai_conversation CASCADE'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE enrollment CASCADE'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE course CASCADE'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE student CASCADE'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE class CASCADE'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE "user" CASCADE'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE ai_config CASCADE'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

CREATE TABLE "user" (
  "id" BIGINT IDENTITY PRIMARY KEY,
  "username" VARCHAR2(64) NOT NULL,
  "password" VARCHAR2(255) NOT NULL,
  "role" VARCHAR2(32) DEFAULT 'ADMIN',
  "created_at" TIMESTAMP DEFAULT SYSDATE
);
CREATE UNIQUE INDEX uk_username ON "user"("username");

INSERT INTO "user" ("username", "password", "role") VALUES ('admin', 'admin', 'ADMIN');

CREATE TABLE "class" (
  "id" BIGINT IDENTITY PRIMARY KEY,
  "class_no" VARCHAR2(64) NOT NULL,
  "name" VARCHAR2(100) NOT NULL,
  "grade" VARCHAR2(20),
  "major" VARCHAR2(100),
  "counselor" VARCHAR2(100),
  "student_count" INT DEFAULT 0,
  "created_at" TIMESTAMP DEFAULT SYSDATE
);
CREATE UNIQUE INDEX uk_class_no ON "class"("class_no");
CREATE INDEX idx_class_major ON "class"("major");
CREATE INDEX idx_class_grade ON "class"("grade");

INSERT INTO "class" ("class_no", "name", "grade", "major", "counselor", "student_count") VALUES ('JSJ-2301', '计科2301班', '2023', '计算机科学与技术', '王老师', 2);
INSERT INTO "class" ("class_no", "name", "grade", "major", "counselor", "student_count") VALUES ('RGC-2302', '软工2302班', '2023', '软件工程', '李老师', 0);
INSERT INTO "class" ("class_no", "name", "grade", "major", "counselor", "student_count") VALUES ('DSJ-2301', '大数据2301班', '2023', '数据科学与大数据技术', '张老师', 0);
INSERT INTO "class" ("class_no", "name", "grade", "major", "counselor", "student_count") VALUES ('AI-2301', '人工智能2301班', '2023', '人工智能', '刘老师', 0);

CREATE TABLE "student" (
  "id" BIGINT IDENTITY PRIMARY KEY,
  "student_no" VARCHAR2(64) NOT NULL,
  "name" VARCHAR2(100) NOT NULL,
  "gender" VARCHAR2(10),
  "age" INT,
  "clazz" VARCHAR2(100),
  "class_id" BIGINT,
  "major" VARCHAR2(100),
  "enroll_date" DATE,
  "phone" VARCHAR2(50),
  "email" VARCHAR2(100),
  "address" VARCHAR2(255)
);
CREATE UNIQUE INDEX uk_student_no ON "student"("student_no");
CREATE INDEX idx_student_name ON "student"("name");
CREATE INDEX idx_student_major ON "student"("major");
CREATE INDEX idx_student_class_id ON "student"("class_id");
CREATE INDEX idx_student_enroll_date ON "student"("enroll_date");
CREATE INDEX idx_student_name_major ON "student"("name", "major");

INSERT INTO "student" ("student_no", "name", "gender", "age", "clazz", "class_id", "major", "enroll_date", "phone", "email") VALUES ('20230001', '张三', '男', 20, '计科2301班', 1, '计算机科学与技术', DATE '2023-09-01', '13800000001', 'zhangsan@stu.edu.cn');
INSERT INTO "student" ("student_no", "name", "gender", "age", "clazz", "class_id", "major", "enroll_date", "phone", "email") VALUES ('20230002', '李四', '女', 19, '计科2301班', 1, '计算机科学与技术', DATE '2023-09-01', '13800000002', 'lisi@stu.edu.cn');
INSERT INTO "student" ("student_no", "name", "gender", "age", "clazz", "class_id", "major", "enroll_date", "phone", "email") VALUES ('20230003', '王五', '男', 21, '软工2302班', 2, '软件工程', DATE '2023-09-01', '13800000003', 'wangwu@stu.edu.cn');
INSERT INTO "student" ("student_no", "name", "gender", "age", "clazz", "class_id", "major", "enroll_date", "phone", "email") VALUES ('20230004', '赵六', '女', 20, '大数据2301班', 3, '数据科学与大数据技术', DATE '2023-09-01', '13800000004', 'zhaoliu@stu.edu.cn');

CREATE TABLE "course" (
  "id" BIGINT IDENTITY PRIMARY KEY,
  "course_no" VARCHAR2(64) NOT NULL,
  "name" VARCHAR2(200) NOT NULL,
  "credit" NUMBER(3,1) DEFAULT 0.0,
  "teacher" VARCHAR2(100),
  "semester" VARCHAR2(50),
  "max_students" INT DEFAULT 60,
  "description" CLOB,
  "created_at" TIMESTAMP DEFAULT SYSDATE
);
CREATE UNIQUE INDEX uk_course_no ON "course"("course_no");
CREATE INDEX idx_course_name ON "course"("name");
CREATE INDEX idx_course_teacher ON "course"("teacher");
CREATE INDEX idx_course_semester ON "course"("semester");

INSERT INTO "course" ("course_no", "name", "credit", "teacher", "semester", "max_students", "description") VALUES ('CS101', '数据结构与算法', 4.0, '陈教授', '2025-2026-1', 80, '线性表、树、图、排序、查找等核心数据结构');
INSERT INTO "course" ("course_no", "name", "credit", "teacher", "semester", "max_students", "description") VALUES ('CS102', '数据库原理与应用', 3.5, '刘教授', '2025-2026-1', 60, '关系数据库理论、SQL语言、数据库设计范式');
INSERT INTO "course" ("course_no", "name", "credit", "teacher", "semester", "max_students", "description") VALUES ('CS103', '最优化理论及应用', 3.0, '赵教授', '2025-2026-1', 50, '线性规划、非线性规划、智能优化算法');
INSERT INTO "course" ("course_no", "name", "credit", "teacher", "semester", "max_students", "description") VALUES ('CS201', '软件工程', 3.0, '王教授', '2025-2026-2', 70, '软件开发流程、UML建模、敏捷开发方法');
INSERT INTO "course" ("course_no", "name", "credit", "teacher", "semester", "max_students", "description") VALUES ('CS202', '人工智能导论', 3.5, '李教授', '2025-2026-2', 60, '机器学习、深度学习、自然语言处理基础');
INSERT INTO "course" ("course_no", "name", "credit", "teacher", "semester", "max_students", "description") VALUES ('CS203', '计算机网络', 3.5, '周教授', '2025-2026-2', 80, 'TCP/IP协议栈、网络拓扑、网络安全');

CREATE TABLE "enrollment" (
  "id" BIGINT IDENTITY PRIMARY KEY,
  "student_id" BIGINT NOT NULL,
  "course_id" BIGINT NOT NULL,
  "enroll_date" DATE,
  "score" NUMBER(5,2),
  "status" VARCHAR2(20) DEFAULT 'ENROLLED'
);
CREATE UNIQUE INDEX uk_student_course ON "enrollment"("student_id", "course_id");
CREATE INDEX idx_enrollment_student ON "enrollment"("student_id");
CREATE INDEX idx_enrollment_course ON "enrollment"("course_id");
CREATE INDEX idx_enrollment_score ON "enrollment"("score");
CREATE INDEX idx_enrollment_status ON "enrollment"("status");

INSERT INTO "enrollment" ("student_id", "course_id", "enroll_date", "score", "status") VALUES (1, 1, DATE '2025-09-01', 85.5, 'COMPLETED');
INSERT INTO "enrollment" ("student_id", "course_id", "enroll_date", "score", "status") VALUES (1, 2, DATE '2025-09-01', 92.0, 'COMPLETED');
INSERT INTO "enrollment" ("student_id", "course_id", "enroll_date", "score", "status") VALUES (1, 3, DATE '2025-09-01', 78.5, 'ENROLLED');
INSERT INTO "enrollment" ("student_id", "course_id", "enroll_date", "score", "status") VALUES (2, 1, DATE '2025-09-01', 90.0, 'COMPLETED');
INSERT INTO "enrollment" ("student_id", "course_id", "enroll_date", "score", "status") VALUES (2, 2, DATE '2025-09-01', 88.5, 'ENROLLED');

CREATE TABLE "ai_conversation" (
  "id" BIGINT IDENTITY PRIMARY KEY,
  "user_id" BIGINT NOT NULL,
  "role" VARCHAR2(20) NOT NULL,
  "content" CLOB NOT NULL,
  "model" VARCHAR2(100),
  "created_at" TIMESTAMP DEFAULT SYSDATE
);
CREATE INDEX idx_conv_user ON "ai_conversation"("user_id");
CREATE INDEX idx_conv_created ON "ai_conversation"("created_at");
CREATE INDEX idx_conv_user_created ON "ai_conversation"("user_id", "created_at");

CREATE TABLE "ai_config" (
  "id" BIGINT IDENTITY PRIMARY KEY,
  "user_id" BIGINT NOT NULL,
  "api_key" VARCHAR2(255),
  "base_url" VARCHAR2(500),
  "model" VARCHAR2(100),
  "updated_at" TIMESTAMP DEFAULT SYSDATE
);
CREATE UNIQUE INDEX uk_ai_config_user ON "ai_config"("user_id");
