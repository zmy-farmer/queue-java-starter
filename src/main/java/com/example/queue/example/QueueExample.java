package com.example.queue.example;

import com.example.queue.core.QueueType;
import com.example.queue.model.QueueMessage;
import com.example.queue.router.QueueRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 队列使用示例
 */
@Slf4j
@Component
public class QueueExample {
    
    @Autowired
    private QueueRouter queueRouter;
    
    /**
     * 演示Java队列的使用
     */
    public void demonstrateJavaQueue() {
        log.info("=== Java队列演示 ===");
        
        // 切换到Java队列
        queueRouter.switchQueue(QueueType.JAVA, "java-demo-queue");
        
        // 发送消息
        QueueMessage message = new QueueMessage(
            UUID.randomUUID().toString(),
            "Java队列测试消息",
            "JAVA_DEMO"
        );
        
        boolean sendResult = queueRouter.sendMessage(message);
        log.info("Java队列发送消息: {}", sendResult ? "成功" : "失败");
        
        // 接收消息
        QueueMessage receivedMessage = queueRouter.receiveMessage();
        if (receivedMessage != null) {
            log.info("Java队列接收消息: {}", receivedMessage.getContent());
        }
        
        // 获取队列信息
        log.info("Java队列大小: {}", queueRouter.getQueueSize());
        log.info("Java队列是否为空: {}", queueRouter.isEmpty());
    }
    
    /**
     * 演示Redis队列的使用
     */
    public void demonstrateRedisQueue() {
        log.info("=== Redis队列演示 ===");
        
        // 切换到Redis队列
        queueRouter.switchQueue(QueueType.REDIS, "redis-demo-queue");
        
        // 发送消息
        QueueMessage message = new QueueMessage(
            UUID.randomUUID().toString(),
            "Redis队列测试消息",
            "REDIS_DEMO"
        );
        
        boolean sendResult = queueRouter.sendMessage(message);
        log.info("Redis队列发送消息: {}", sendResult ? "成功" : "失败");
        
        // 接收消息
        QueueMessage receivedMessage = queueRouter.receiveMessage();
        if (receivedMessage != null) {
            log.info("Redis队列接收消息: {}", receivedMessage.getContent());
        }
        
        // 获取队列信息
        log.info("Redis队列大小: {}", queueRouter.getQueueSize());
        log.info("Redis队列是否为空: {}", queueRouter.isEmpty());
    }
    
    /**
     * 演示RabbitMQ队列的使用
     */
    public void demonstrateRabbitMQQueue() {
        log.info("=== RabbitMQ队列演示 ===");
        
        // 切换到RabbitMQ队列
        queueRouter.switchQueue(QueueType.RABBITMQ, "rabbitmq-demo-queue");
        
        // 发送消息
        QueueMessage message = new QueueMessage(
            UUID.randomUUID().toString(),
            "RabbitMQ队列测试消息",
            "RABBITMQ_DEMO"
        );
        
        boolean sendResult = queueRouter.sendMessage(message);
        log.info("RabbitMQ队列发送消息: {}", sendResult ? "成功" : "失败");
        
        // 接收消息
        QueueMessage receivedMessage = queueRouter.receiveMessage();
        if (receivedMessage != null) {
            log.info("RabbitMQ队列接收消息: {}", receivedMessage.getContent());
        }
        
        // 获取队列信息
        log.info("RabbitMQ队列大小: {}", queueRouter.getQueueSize());
        log.info("RabbitMQ队列是否为空: {}", queueRouter.isEmpty());
    }
    
    /**
     * 演示批量消息操作
     */
    public void demonstrateBatchOperations() {
        log.info("=== 批量操作演示 ===");
        
        // 切换到Java队列进行批量操作
        queueRouter.switchQueue(QueueType.JAVA, "batch-demo-queue");
        
        // 创建批量消息
        List<QueueMessage> messages = Arrays.asList(
            new QueueMessage(UUID.randomUUID().toString(), "批量消息1", "BATCH"),
            new QueueMessage(UUID.randomUUID().toString(), "批量消息2", "BATCH"),
            new QueueMessage(UUID.randomUUID().toString(), "批量消息3", "BATCH")
        );
        
        // 批量发送
        int sentCount = queueRouter.sendMessages(messages);
        log.info("批量发送消息: {}/{}", sentCount, messages.size());
        
        // 批量接收
        List<QueueMessage> receivedMessages = queueRouter.receiveMessages(5);
        log.info("批量接收消息: {}条", receivedMessages.size());
        
        for (QueueMessage msg : receivedMessages) {
            log.info("接收到的消息: {}", msg.getContent());
        }
    }
    
    /**
     * 演示队列切换
     */
    public void demonstrateQueueSwitching() {
        log.info("=== 队列切换演示 ===");
        
        // 切换到Java队列
        queueRouter.switchQueue(QueueType.JAVA, "switch-demo-queue");
        log.info("当前队列: {} - {}", queueRouter.getCurrentQueueType(), queueRouter.getCurrentQueueName());
        
        // 发送消息到Java队列
        QueueMessage message1 = new QueueMessage(UUID.randomUUID().toString(), "Java队列消息", "SWITCH");
        queueRouter.sendMessage(message1);
        
        // 切换到Redis队列
        queueRouter.switchQueue(QueueType.REDIS, "switch-demo-queue");
        log.info("当前队列: {} - {}", queueRouter.getCurrentQueueType(), queueRouter.getCurrentQueueName());
        
        // 发送消息到Redis队列
        QueueMessage message2 = new QueueMessage(UUID.randomUUID().toString(), "Redis队列消息", "SWITCH");
        queueRouter.sendMessage(message2);
        
        // 切换回Java队列接收消息
        queueRouter.switchQueue(QueueType.JAVA, "switch-demo-queue");
        QueueMessage receivedMessage = queueRouter.receiveMessage();
        if (receivedMessage != null) {
            log.info("从Java队列接收消息: {}", receivedMessage.getContent());
        }
        
        // 切换到Redis队列接收消息
        queueRouter.switchQueue(QueueType.REDIS, "switch-demo-queue");
        receivedMessage = queueRouter.receiveMessage();
        if (receivedMessage != null) {
            log.info("从Redis队列接收消息: {}", receivedMessage.getContent());
        }
    }
    
    /**
     * 运行所有演示
     */
    public void runAllDemonstrations() {
        log.info("开始队列路由器演示...");
        
        try {
            demonstrateJavaQueue();
            demonstrateRedisQueue();
            demonstrateRabbitMQQueue();
            demonstrateBatchOperations();
            demonstrateQueueSwitching();
            
            log.info("队列路由器演示完成！");
        } catch (Exception e) {
            log.error("演示过程中发生错误: {}", e.getMessage(), e);
        }
    }
}
