package com.example.queue;

import com.example.queue.core.QueueType;
import com.example.queue.factory.QueueServiceFactory;
import com.example.queue.model.QueueMessage;
import com.example.queue.router.QueueRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 队列路由器测试
 */
@ExtendWith(MockitoExtension.class)
class QueueRouterTest {
    
    @Mock
    private StringRedisTemplate redisTemplate;
    
    @Mock
    private RabbitTemplate rabbitTemplate;
    
    private QueueRouter queueRouter;
    private QueueServiceFactory queueServiceFactory;
    
    @BeforeEach
    void setUp() {
        queueServiceFactory = new QueueServiceFactory(redisTemplate, rabbitTemplate);
        queueRouter = new QueueRouter(queueServiceFactory);
    }
    
    @Test
    void testSwitchToJavaQueue() {
        // 切换到Java队列
        queueRouter.switchQueue(QueueType.JAVA, "test-queue");
        
        assertEquals(QueueType.JAVA, queueRouter.getCurrentQueueType());
        assertEquals("test-queue", queueRouter.getCurrentQueueName());
    }
    
    @Test
    void testSwitchToRedisQueue() {
        // 切换到Redis队列
        queueRouter.switchQueue(QueueType.REDIS, "redis-queue");
        
        assertEquals(QueueType.REDIS, queueRouter.getCurrentQueueType());
        assertEquals("redis-queue", queueRouter.getCurrentQueueName());
    }
    
    @Test
    void testSwitchToRabbitMQQueue() {
        // 切换到RabbitMQ队列
        queueRouter.switchQueue(QueueType.RABBITMQ, "rabbitmq-queue");
        
        assertEquals(QueueType.RABBITMQ, queueRouter.getCurrentQueueType());
        assertEquals("rabbitmq-queue", queueRouter.getCurrentQueueName());
    }
    
    @Test
    void testSendAndReceiveMessage() {
        // 切换到Java队列进行测试
        queueRouter.switchQueue(QueueType.JAVA, "test-queue");
        
        // 创建测试消息
        QueueMessage message = new QueueMessage(
            UUID.randomUUID().toString(),
            "测试消息内容",
            "TEST"
        );
        
        // 发送消息
        boolean sendResult = queueRouter.sendMessage(message);
        assertTrue(sendResult, "消息发送应该成功");
        
        // 接收消息
        QueueMessage receivedMessage = queueRouter.receiveMessage();
        assertNotNull(receivedMessage, "应该能接收到消息");
        assertEquals(message.getMessageId(), receivedMessage.getMessageId());
        assertEquals(message.getContent(), receivedMessage.getContent());
        assertEquals(message.getMessageType(), receivedMessage.getMessageType());
    }
    
    @Test
    void testBatchSendAndReceive() {
        // 切换到Java队列进行测试
        queueRouter.switchQueue(QueueType.JAVA, "batch-test-queue");
        
        // 创建批量消息
        List<QueueMessage> messages = Arrays.asList(
            new QueueMessage(UUID.randomUUID().toString(), "消息1", "BATCH"),
            new QueueMessage(UUID.randomUUID().toString(), "消息2", "BATCH"),
            new QueueMessage(UUID.randomUUID().toString(), "消息3", "BATCH")
        );
        
        // 批量发送
        int sentCount = queueRouter.sendMessages(messages);
        assertEquals(3, sentCount, "应该发送3条消息");
        
        // 批量接收
        List<QueueMessage> receivedMessages = queueRouter.receiveMessages(5);
        assertEquals(3, receivedMessages.size(), "应该接收到3条消息");
    }
    
    @Test
    void testQueueSize() {
        // 切换到Java队列进行测试
        queueRouter.switchQueue(QueueType.JAVA, "size-test-queue");
        
        // 初始队列应该为空
        assertTrue(queueRouter.isEmpty(), "初始队列应该为空");
        assertEquals(0, queueRouter.getQueueSize(), "初始队列大小应该为0");
        
        // 发送消息后队列大小应该增加
        QueueMessage message = new QueueMessage(UUID.randomUUID().toString(), "测试消息");
        queueRouter.sendMessage(message);
        
        assertFalse(queueRouter.isEmpty(), "发送消息后队列不应该为空");
        assertEquals(1, queueRouter.getQueueSize(), "队列大小应该为1");
    }
    
    @Test
    void testClearQueue() {
        // 切换到Java队列进行测试
        queueRouter.switchQueue(QueueType.JAVA, "clear-test-queue");
        
        // 发送一些消息
        for (int i = 0; i < 5; i++) {
            QueueMessage message = new QueueMessage(UUID.randomUUID().toString(), "消息" + i);
            queueRouter.sendMessage(message);
        }
        
        assertEquals(5, queueRouter.getQueueSize(), "队列应该有5条消息");
        
        // 清空队列
        boolean clearResult = queueRouter.clearQueue();
        assertTrue(clearResult, "清空队列应该成功");
        
        assertTrue(queueRouter.isEmpty(), "清空后队列应该为空");
        assertEquals(0, queueRouter.getQueueSize(), "清空后队列大小应该为0");
    }
    
    @Test
    void testSwitchQueueWithString() {
        // 使用字符串切换队列
        queueRouter.switchQueue("redis", "string-test-queue");
        
        assertEquals(QueueType.REDIS, queueRouter.getCurrentQueueType());
        assertEquals("string-test-queue", queueRouter.getCurrentQueueName());
    }
    
    @Test
    void testInvalidQueueType() {
        // 测试无效的队列类型，应该使用默认的Java队列
        queueRouter.switchQueue("invalid", "invalid-queue");
        
        assertEquals(QueueType.JAVA, queueRouter.getCurrentQueueType());
        assertEquals("invalid-queue", queueRouter.getCurrentQueueName());
    }
}
