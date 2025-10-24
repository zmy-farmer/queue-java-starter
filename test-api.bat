@echo off
echo 队列路由器API测试脚本
echo.

set BASE_URL=http://localhost:8080/api/queue

echo 1. 获取支持的队列类型...
curl -X GET "%BASE_URL%/types"
echo.
echo.

echo 2. 切换到Java队列...
curl -X POST "%BASE_URL%/switch?queueType=java&queueName=test-queue"
echo.
echo.

echo 3. 发送消息到Java队列...
curl -X POST "%BASE_URL%/send" -H "Content-Type: application/json" -d "{\"content\":\"测试消息内容\",\"messageType\":\"TEST\"}"
echo.
echo.

echo 4. 获取队列信息...
curl -X GET "%BASE_URL%/info"
echo.
echo.

echo 5. 接收消息...
curl -X GET "%BASE_URL%/receive"
echo.
echo.

echo 6. 切换到Redis队列...
curl -X POST "%BASE_URL%/switch?queueType=redis&queueName=redis-test-queue"
echo.
echo.

echo 7. 发送消息到Redis队列...
curl -X POST "%BASE_URL%/send" -H "Content-Type: application/json" -d "{\"content\":\"Redis测试消息\",\"messageType\":\"REDIS_TEST\"}"
echo.
echo.

echo 8. 获取Redis队列信息...
curl -X GET "%BASE_URL%/info"
echo.
echo.

echo 9. 接收Redis队列消息...
curl -X GET "%BASE_URL%/receive"
echo.
echo.

echo 10. 清空队列...
curl -X POST "%BASE_URL%/clear"
echo.
echo.

echo API测试完成！
pause
