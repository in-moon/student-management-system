# 智能学生管理系统

> **课程**：最优化理论及应用 — 数据库课程设计  
> **技术栈**：Vue3 + SpringBoot3 + MyBatis-Plus + MySQL + Redis + AI  
> **数据库**：MySQL 8.0（可扩展达梦 DM8）

---

## 功能概览

| 模块 | 功能 |
|------|------|
| 🔐 用户认证 | JWT 登录鉴权，BCrypt 密码加密，明文密码自动升级 |
| 👨‍🎓 学生管理 | 增删查改、分页搜索、多条件筛选、批量操作 |
| 📚 班级管理 | 班级 CRUD、辅导员分配、年级/专业筛选 |
| 📖 课程管理 | 课程全生命周期管理、学分/学期/教师设置 |
| 📝 选课管理 | 学生选课/退课、成绩录入、防重复选课 |
| 🤖 AI 智能助手 | OpenAI 兼容大模型对话，SSE 流式输出，用户可自行配置 API Key |
| ⚡ Redis 缓存 | 热点数据缓存，自动过期刷新 |
| 🗄️ 国产数据库 | 达梦 DM8 驱动已集成，Profile 一键切换 |

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Vite + Element Plus + TypeScript + Axios |
| 后端 | Spring Boot 3.3 + MyBatis-Plus 3.5 |
| 数据库 | MySQL 8.0（可选达梦 DM8） |
| 缓存 | Redis 7（Lettuce 连接池） |
| 安全 | JWT + BCrypt |
| AI | OpenAI 兼容 API（SSE 流式） |
| 文档 | SpringDoc OpenAPI（Swagger） |

## 一键启动（推荐）

```bash
# Windows 上双击运行，自动检查环境、创建数据库、启动前后端
start.bat
```

脚本会自动：检查 JDK/Maven/Node/MySQL → 创建数据库 → 改配置 → 启动后端 → 启动前端 → 打开浏览器。

---

## 手动启动

### 第一步：安装环境

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| Node.js | 18+ | `node -version` |
| MySQL | 8.0+ | 需创建空数据库 `student_system` |
| Redis | 7.x | 可选，没有也能跑 |

### 第二步：创建数据库

```sql
-- 用 MySQL 客户端执行
CREATE DATABASE IF NOT EXISTS student_system DEFAULT CHARACTER SET utf8mb4;
```

### 第三步：修改配置

编辑 `backend/src/main/resources/application.yml`，改两处：

```yaml
spring:
  datasource:
    username: 你的MySQL用户名
    password: 你的MySQL密码
```

### 第四步：启动

```bash
# 终端1：后端（自动建表，首次启动稍慢）
cd backend
mvn spring-boot:run
# 看到 "Started StudentSystemApplication" 即成功

# 终端2：前端
cd frontend
npm install        # 仅首次
npm run dev
```

### 第五步：打开浏览器

- **前端页面**：http://localhost:5173
- **Swagger 文档**：http://localhost:8088/swagger-ui.html
- **默认账号**：`admin` / `admin`

> 首次登录密码明文存储，登录后自动升级为 BCrypt 加密。

## API 速览

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/login` | 登录获取 JWT |
| GET | `/api/students` | 学生列表（分页/搜索/筛选） |
| POST/PUT/DELETE | `/api/students[/{id}]` | 学生增删改 |
| GET | `/api/classes` | 班级列表 |
| POST/PUT/DELETE | `/api/classes[/{id}]` | 班级增删改 |
| GET/POST/PUT/DELETE | `/api/courses[/{id}]` | 课程 CRUD |
| GET/POST/DELETE | `/api/enrollments[/{id}]` | 选课管理 |
| POST | `/api/ai/chat` | AI 流式对话（SSE） |
| GET/PUT/DELETE | `/api/ai/config` | AI 配置（API Key/模型） |
| GET/DELETE | `/api/ai/history` | AI 对话历史 |

## 数据库初始化

首次启动时自动执行 `schema.sql`，创建 6 张表并插入测试数据：

- `user` / `student` / `class` / `course` / `enrollment` / `ai_conversation` / `ai_config`

## AI 配置

在 AI 对话页面点击右上角 ⚙️ 图标，配置 API Key 即可使用真实大模型：

- 支持 OpenAI / DeepSeek / 通义千问 / GLM-4 等
- 不配置也可使用内置降级模式

## 国产数据库扩展

项目已预置达梦 DM8 支持：

```bash
# 切换 DM8
mvn spring-boot:run -Dspring-boot.run.profiles=dm8
```

- DM8 JDBC 驱动已集成（Maven Central）
- DDL 脚本已适配：`schema-dm8.sql`
- 配置文件：`application-dm8.yml`

## 项目结构

```
student-management-system/
├── backend/
│   ├── src/main/java/com/example/studentsystem/
│   │   ├── config/          # AiConfig, RedisConfig, MyBatisPlus, Cors, Web
│   │   ├── entity/          # Student, User, ClassEntity, Course, Enrollment, AiConversation, AiConfigEntity
│   │   ├── mapper/          # MyBatis-Plus BaseMapper
│   │   ├── security/        # JwtUtil, JwtAuthFilter
│   │   ├── service/         # 业务服务接口 + 实现
│   │   └── web/
│   │       ├── controller/  # Auth, Student, Class, Course, Enrollment, AiChat, AiConfig
│   │       └── dto/         # 请求/响应 DTO
│   ├── src/main/resources/
│   │   ├── application.yml       # 默认 MySQL 配置
│   │   ├── application-dm8.yml   # DM8 Profile（可选）
│   │   ├── schema.sql            # MySQL DDL
│   │   └── schema-dm8.sql        # DM8 DDL（可选）
│   └── pom.xml
├── frontend/
│   └── src/
│       ├── api/      # HTTP 接口封装
│       ├── layouts/  # 主布局
│       ├── pages/    # 页面组件
│       │   ├── ai/        # AI 对话
│       │   ├── students/  # 学生管理
│       │   ├── classes/   # 班级管理
│       │   └── courses/   # 课程管理
│       └── router/  # 路由配置
└── 项目报告.md       # 完整课程设计报告
```

## 许可证

仅供学习用途。
