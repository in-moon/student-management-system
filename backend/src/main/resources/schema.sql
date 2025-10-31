DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `student_no` VARCHAR(64) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `gender` VARCHAR(10) DEFAULT NULL,
  `age` INT DEFAULT NULL,
  `clazz` VARCHAR(100) DEFAULT NULL,
  `major` VARCHAR(100) DEFAULT NULL,
  `enroll_date` DATE DEFAULT NULL,
  `phone` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_no` (`student_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- demo data
INSERT INTO `student` (`student_no`, `name`, `gender`, `age`, `clazz`, `major`, `enroll_date`, `phone`) VALUES
('20230001', '张三', '男', 20, '计科2301', '计算机科学与技术', '2023-09-01', '13800000001'),
('20230002', '李四', '女', 19, '软工2302', '软件工程', '2023-09-01', '13800000002');

-- user table for authentication
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(64) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `role` VARCHAR(32) DEFAULT 'ADMIN',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- demo admin (username: admin, password: admin)
INSERT INTO `user` (`username`, `password`, `role`) VALUES ('admin', 'admin', 'ADMIN');

-- class table for class management
DROP TABLE IF EXISTS `class`;
CREATE TABLE `class` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `class_no` VARCHAR(64) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `grade` VARCHAR(20) DEFAULT NULL,
  `major` VARCHAR(100) DEFAULT NULL,
  `counselor` VARCHAR(100) DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_no` (`class_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- demo classes
INSERT INTO `class` (`class_no`, `name`, `grade`, `major`, `counselor`) VALUES
('JSJ-2301', '计科2301班', '2023', '计算机科学与技术', '王老师'),
('RGC-2302', '软工2302班', '2023', '软件工程', '李老师');


