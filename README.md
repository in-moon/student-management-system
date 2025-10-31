## 学生管理系统（Vue 3 + Element Plus + Spring Boot 3）

### 概述
一个前后端分离的学生管理系统，包含登录鉴权（JWT）、学生管理与班级管理的完整 CRUD、搜索、分页、排序等功能。支持 Excel 导入/导出（预留位，待扩展）。

### 技术栈
- 前端：Vue 3、Vite、Element Plus、Vue Router、Pinia、Axios
- 后端：Spring Boot 3、MyBatis-Plus、MySQL、Springdoc OpenAPI、Lombok、JJWT

### 目录结构
```
student-system/
├─ backend/
│  ├─ src/main/java/com/example/studentsystem/
│  │  ├─ StudentSystemApplication.java
│  │  ├─ config/
│  │  │  ├─ CorsConfig.java
│  │  │  ├─ MybatisPlusConfig.java
│  │  │  └─ WebConfig.java
│  │  ├─ security/
│  │  │  ├─ JwtAuthFilter.java
│  │  │  └─ JwtUtil.java
│  │  ├─ entity/ (Student、User、ClassEntity)
│  │  ├─ mapper/ (StudentMapper、UserMapper、ClassMapper)
│  │  ├─ service/ + impl/ (StudentService、ClassService)
│  │  └─ web/
│  │     ├─ controller/ (AuthController、StudentController、ClassController)
│  │     └─ dto/ (LoginRequest、StudentQuery、ClassQuery、PageResponse)
│  ├─ src/main/resources/
│  │  ├─ application.yml
│  │  └─ schema.sql
│  └─ pom.xml
└─ frontend/
   ├─ src/
   │  ├─ main.ts、App.vue
   │  ├─ layouts/MainLayout.vue
   │  ├─ router/index.ts
   │  ├─ api/ (http.ts、student.ts、class.ts)
   │  └─ pages/
   │     ├─ Login.vue
   │     └─ students/、classes/（列表/详情/表单）
   ├─ vite.config.ts、tsconfig.json、index.html、package.json
```

### 数据库初始化
1) 本地 MySQL 创建数据库：`student_system`
2) 修改 `backend/src/main/resources/application.yml` 的数据库账号密码（默认 `root/root`）
3) 首次启动后端时，会自动执行 `schema.sql`：
   - 表：`student`、`user`、`class`
   - 初始化用户：`admin / admin`

### 启动方式
前置：Node 18+/pnpm 或 npm、JDK 17、Maven 3.8+、MySQL 8+

- 后端（在 backend 目录）
  - 开发运行：`mvn spring-boot:run`
  - 构建 Jar：`mvn clean package -DskipTests`
  - 运行 Jar：`java -jar target/student-system-0.0.1-SNAPSHOT.jar`

- 前端（在 frontend 目录）
  - 安装依赖：`npm i`
  - 本地开发：`npm run dev`（默认 5173 端口，代理 `/api` 到 8080）
  - 生产构建：`npm run build`
  - 本地预览：`npm run preview`

### 账号
- 管理员账号：`admin`
- 密码：`admin`

### API 速览（REST）
- 登录：POST `/api/auth/login`（返回 `token`）
- 学生：
  - GET `/api/students`（分页、关键词、性别、专业、排序）
  - GET `/api/students/{id}`
  - POST `/api/students`、PUT `/api/students/{id}`、DELETE `/api/students/{id}`
  - DELETE `/api/students`（Body: `[1,2,3]` 批量）
- 班级：
  - GET `/api/classes`（分页、关键词、年级、排序）
  - GET `/api/classes/{id}`
  - POST `/api/classes`、PUT `/api/classes/{id}`、DELETE `/api/classes/{id}`
  - DELETE `/api/classes`（Body: `[1,2,3]` 批量）

### 鉴权说明（JWT）
- 登录成功后返回 JWT，前端通过 Axios 拦截器在请求头携带 `Authorization: Bearer <token>`
- 后端 `JwtAuthFilter` 仅放行：`/api/auth/login`、OpenAPI 文档、以及浏览器 OPTIONS 预检
- 其余 `/api/**` 必须携带有效 JWT

### 常见问题
- 无法解析依赖（国内网络）：
  - MySQL 驱动已固定为 `com.mysql:mysql-connector-j:8.0.33`
  - 执行 `mvn -U clean package -DskipTests` 强制更新
- Mapper 扫描问题：
  - 应用入口已添加 `@MapperScan("com.example.studentsystem.mapper")`
- 参数名异常：
  - `maven-compiler-plugin` 已启用 `<parameters>true</parameters>`；控制器也显式 `@PathVariable("id")`
- 鉴权 401：
  - 确保请求头包含 `Authorization: Bearer <token>`；如有过期/旧 token，重新登录
- 前端路由进入详情/编辑异常：
  - 已调整路由匹配顺序（编辑在详情前）
- 批量删除失败：
  - 前端 DELETE 携带 body 时已自动设置 `Content-Type: application/json`

### Swagger 文档
- 访问 `http://localhost:8080/swagger-ui.html`

### 后续扩展建议
- Excel 导入/导出（EasyExcel/POI）
- 用户密码加密存储（BCrypt）与角色权限
- 教师、课程模块
- 前端表单校验规则细化与国际化、多主题

### 许可证
本项目示例用途，按需修改使用。


