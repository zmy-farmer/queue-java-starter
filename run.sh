#!/bin/bash

echo "启动队列路由器测试项目..."

echo ""
echo "1. 编译项目..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "编译失败！"
    exit 1
fi

echo ""
echo "2. 运行测试..."
mvn test

if [ $? -ne 0 ]; then
    echo "测试失败！"
    exit 1
fi

echo ""
echo "3. 启动应用..."
echo "应用将在 http://localhost:8080 启动"
echo "按 Ctrl+C 停止应用"
echo ""

mvn spring-boot:run
