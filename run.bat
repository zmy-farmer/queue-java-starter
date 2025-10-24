@echo off
echo 启动队列路由器测试项目...

echo.
echo 1. 编译项目...
call mvn clean compile

if %ERRORLEVEL% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo.
echo 2. 运行测试...
call mvn test

if %ERRORLEVEL% neq 0 (
    echo 测试失败！
    pause
    exit /b 1
)

echo.
echo 3. 启动应用...
echo 应用将在 http://localhost:8080 启动
echo 按 Ctrl+C 停止应用
echo.

call mvn spring-boot:run

pause
