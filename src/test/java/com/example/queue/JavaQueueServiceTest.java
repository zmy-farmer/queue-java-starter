package com.example.queue;

import com.example.queue.impl.JavaQueueService;
import com.example.queue.model.QueueMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Java队列服务测试
 */
class JavaQueueServiceTest {
    
    private JavaQueueService javaQueueService;
    
    @BeforeEach
    void setUp() {
        javaQueueService = new JavaQueueService("test-queue");
    }
    
    @Test
    void testSendAndReceiveMessage() {
        // 创建测试消息
        QueueMessage message = new QueueMessage(
            UUID.randomUUID().toString(),
            "测试消息内容",
            "TEST"
        );
        
        // 发送消息
        boolean sendResult = javaQueueService.sendMessage(message);
        assertTrue(sendResult, "消息发送应该成功");
        
        // 接收消息
        QueueMessage receivedMessage = javaQueueService.receiveMessage();
        assertNotNull(receivedMessage, "应该能接收到消息");
        assertEquals(message.getMessageId(), receivedMessage.getMessageId());
        assertEquals(message.getContent(), receivedMessage.getContent());
        assertEquals(message.getMessageType(), receivedMessage.getMessageType());
    }
    
    @Test
    void testSendNullMessage() {
        // 发送空消息应该返回false
        boolean result = javaQueueService.sendMessage(null);
        assertFalse(result, "发送空消息应该返回false");
    }
    
    @Test
    void testReceiveFromEmptyQueue() {
        // 从空队列接收消息应该返回null
        QueueMessage message = javaQueueService.receiveMessage();
        assertNull(message, "从空队列接收消息应该返回null");
    }
    
    @Test
    void testBatchSendAndReceive() {
        // 创建批量消息
        List<QueueMessage> messages = Arrays.asList(
            new QueueMessage(UUID.randomUUID().toString(), "消息1", "BATCH"),
            new QueueMessage(UUID.randomUUID().toString(), "消息2", "BATCH"),
            new QueueMessage(UUID.randomUUID().toString(), "消息3", "BATCH")
        );
        
        // 批量发送
        int sentCount = javaQueueService.sendMessages(messages);
        assertEquals(3, sentCount, "应该发送3条消息");
        
        // 批量接收
        List<QueueMessage> receivedMessages = javaQueueService.receiveMessages(5);
        assertEquals(3, receivedMessages.size(), "应该接收到3条消息");
    }
    
    @Test
    void testQueueSize() {
        // 初始队列应该为空
        assertTrue(javaQueueService.isEmpty(), "初始队列应该为空");
        assertEquals(0, javaQueueService.getQueueSize(), "初始队列大小应该为0");
        
        // 发送消息后队列大小应该增加
        QueueMessage message = new QueueMessage(UUID.randomUUID().toString(), "测试消息");
        javaQueueService.sendMessage(message);
        
        assertFalse(javaQueueService.isEmpty(), "发送消息后队列不应该为空");
        assertEquals(1, javaQueueService.getQueueSize(), "队列大小应该为1");
    }
    
    @Test
    void testClearQueue() {
        // 发送一些消息
        for (int i = 0; i < 5; i++) {
            QueueMessage message = new QueueMessage(UUID.randomUUID().toString(), "消息" + i);
            javaQueueService.sendMessage(message);
        }
        
        assertEquals(5, javaQueueService.getQueueSize(), "队列应该有5条消息");
        
        // 清空队列
        boolean clearResult = javaQueueService.clearQueue();
        assertTrue(clearResult, "清空队列应该成功");
        
        assertTrue(javaQueueService.isEmpty(), "清空后队列应该为空");
        assertEquals(0, javaQueueService.getQueueSize(), "清空后队列大小应该为0");
    }
    
    @Test
    void testReceiveMessageWithTimeout() {
        // 测试带超时的接收消息
        QueueMessage message = new QueueMessage(UUID.randomUUID().toString(), "超时测试消息");
        javaQueueService.sendMessage(message);
        
        QueueMessage receivedMessage = javaQueueService.receiveMessage(1);
        assertNotNull(receivedMessage, "应该能接收到消息");
        assertEquals(message.getMessageId(), receivedMessage.getMessageId());
    }
    
    @Test
    void testAsyncSendMessage() throws Exception {
        // 测试异步发送消息
        QueueMessage message = new QueueMessage(UUID.randomUUID().toString(), "异步测试消息");
        
        boolean result = javaQueueService.sendMessageAsync(message).get();
        assertTrue(result, "异步发送消息应该成功");
        
        QueueMessage receivedMessage = javaQueueService.receiveMessage();
        assertNotNull(receivedMessage, "应该能接收到异步发送的消息");
        assertEquals(message.getMessageId(), receivedMessage.getMessageId());
    }
    
    @Test
    void testQueueType() {
        assertEquals("JAVA", javaQueueService.getQueueType(), "队列类型应该是JAVA");
    }
}
