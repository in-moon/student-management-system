@echo off
chcp 65001 >nul
title 智能学生管理系统 - 一键启动

echo ========================================
echo   智能学生管理系统 - 环境检查 + 启动
echo ========================================
echo.

REM === 检查 Java ===
echo [1/5] 检查 Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo   [错误] 未找到 Java！请安装 JDK 17+
    pause
    exit /b 1
)
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr /i "version"') do echo   Java: %%i

REM === 检查 Maven ===
echo [2/5] 检查 Maven...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo   [警告] 未找到 Maven，尝试使用 mvnw...
    if exist "backend\mvnw.cmd" (
        set MVN_CMD=backend\mvnw.cmd
    ) else (
        echo   [错误] 请安装 Maven 3.8+
        pause
        exit /b 1
    )
) else (
    set MVN_CMD=mvn
)
echo   Maven: OK

REM === 检查 Node ===
echo [3/5] 检查 Node.js...
node -version >nul 2>&1
if errorlevel 1 (
    echo   [错误] 未找到 Node.js！请安装 Node 18+
    pause
    exit /b 1
)
for /f "tokens=*" %%i in ('node -version') do echo   Node: %%i

REM === 检查 MySQL ===
echo [4/5] 检查 MySQL 连接...
set /p MYSQL_USER="请输入 MySQL 用户名 (默认 root): " || set MYSQL_USER=root
set /p MYSQL_PASS="请输入 MySQL 密码: "
echo.
echo   尝试创建数据库 student_system...

mysql -u %MYSQL_USER% -p%MYSQL_PASS% -e "CREATE DATABASE IF NOT EXISTS student_system DEFAULT CHARACTER SET utf8mb4;" 2>nul
if errorlevel 1 (
    echo   [警告] 无法自动创建数据库，请手动执行:
    echo     CREATE DATABASE IF NOT EXISTS student_system DEFAULT CHARACTER SET utf8mb4;
    echo   然后修改 backend\src\main\resources\application.yml 中的用户名密码
    echo   按任意键继续...
    pause >nul
) else (
    echo   数据库 student_system: OK
)

REM === 更新配置 ===
echo [5/5] 更新数据库配置...
powershell -Command "(Get-Content backend\src\main\resources\application.yml) -replace 'username: root', 'username: %MYSQL_USER%' -replace 'password: admin', 'password: %MYSQL_PASS%' | Set-Content backend\src\main\resources\application.yml" 2>nul
echo   配置已更新

echo.
echo ========================================
echo   启动服务...
echo ========================================

REM === 启动后端 ===
echo.
echo 启动后端 (SpringBoot)...
start "学生管理系统-后端" cmd /c "cd /d %~dp0backend && %MVN_CMD% spring-boot:run"
echo   后端启动中，等待约 15 秒...

REM === 等待后端就绪 ===
echo   等待后端就绪...
:wait_backend
timeout /t 3 /nobreak >nul
curl -s http://localhost:8088/actuator >nul 2>&1
if errorlevel 1 (
    curl -s http://localhost:8088/swagger-ui.html >nul 2>&1
    if errorlevel 1 goto wait_backend
)
echo   后端就绪: http://localhost:8088

REM === 安装前端依赖 (首次) ===
echo.
echo 检查前端依赖...
if not exist "frontend\node_modules" (
    echo   首次运行，安装前端依赖 (约 1-2 分钟)...
    cd /d "%~dp0frontend"
    call npm install
    cd /d "%~dp0"
)

REM === 启动前端 ===
echo 启动前端 (Vue3)...
start "学生管理系统-前端" cmd /c "cd /d %~dp0frontend && npm run dev"

REM === 等待前端就绪 ===
echo   等待前端就绪...
:wait_frontend
timeout /t 2 /nobreak >nul
curl -s http://localhost:5173 >nul 2>&1
if errorlevel 1 goto wait_frontend

REM === 打开浏览器 ===
echo.
echo ========================================
echo   启动完成！
echo   前端: http://localhost:5173
echo   Swagger: http://localhost:8088/swagger-ui.html
echo   账号: admin / admin
echo ========================================
echo.
start http://localhost:5173

pause
