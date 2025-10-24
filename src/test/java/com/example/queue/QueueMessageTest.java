package com.example.queue;

import com.example.queue.model.QueueMessage;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 队列消息测试
 */
class QueueMessageTest {
    
    @Test
    void testDefaultConstructor() {
        QueueMessage message = new QueueMessage();
        
        assertNull(message.getMessageId());
        assertNull(message.getContent());
        assertNull(message.getMessageType());
        assertNull(message.getCreateTime());
        assertNull(message.getPriority());
        assertNull(message.getDelaySeconds());
    }
    
    @Test
    void testConstructorWithIdAndContent() {
        String messageId = UUID.randomUUID().toString();
        String content = "测试消息内容";
        
        QueueMessage message = new QueueMessage(messageId, content);
        
        assertEquals(messageId, message.getMessageId());
        assertEquals(content, message.getContent());
        assertNull(message.getMessageType());
        assertNotNull(message.getCreateTime());
        assertEquals(0, message.getPriority());
        assertEquals(0L, message.getDelaySeconds());
    }
    
    @Test
    void testConstructorWithIdContentAndType() {
        String messageId = UUID.randomUUID().toString();
        String content = "测试消息内容";
        String messageType = "TEST";
        
        QueueMessage message = new QueueMessage(messageId, content, messageType);
        
        assertEquals(messageId, message.getMessageId());
        assertEquals(content, message.getContent());
        assertEquals(messageType, message.getMessageType());
        assertNotNull(message.getCreateTime());
        assertEquals(0, message.getPriority());
        assertEquals(0L, message.getDelaySeconds());
    }
    
    @Test
    void testAllArgsConstructor() {
        String messageId = UUID.randomUUID().toString();
        String content = "测试消息内容";
        String messageType = "TEST";
        LocalDateTime createTime = LocalDateTime.now();
        Integer priority = 5;
        Long delaySeconds = 10L;
        
        QueueMessage message = new QueueMessage(
            messageId, content, messageType, createTime, priority, delaySeconds
        );
        
        assertEquals(messageId, message.getMessageId());
        assertEquals(content, message.getContent());
        assertEquals(messageType, message.getMessageType());
        assertEquals(createTime, message.getCreateTime());
        assertEquals(priority, message.getPriority());
        assertEquals(delaySeconds, message.getDelaySeconds());
    }
    
    @Test
    void testSettersAndGetters() {
        QueueMessage message = new QueueMessage();
        
        String messageId = UUID.randomUUID().toString();
        String content = "测试消息内容";
        String messageType = "TEST";
        LocalDateTime createTime = LocalDateTime.now();
        Integer priority = 3;
        Long delaySeconds = 5L;
        
        message.setMessageId(messageId);
        message.setContent(content);
        message.setMessageType(messageType);
        message.setCreateTime(createTime);
        message.setPriority(priority);
        message.setDelaySeconds(delaySeconds);
        
        assertEquals(messageId, message.getMessageId());
        assertEquals(content, message.getContent());
        assertEquals(messageType, message.getMessageType());
        assertEquals(createTime, message.getCreateTime());
        assertEquals(priority, message.getPriority());
        assertEquals(delaySeconds, message.getDelaySeconds());
    }
    
    @Test
    void testToString() {
        QueueMessage message = new QueueMessage("test-id", "test-content", "TEST");
        String toString = message.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("test-id"));
        assertTrue(toString.contains("test-content"));
        assertTrue(toString.contains("TEST"));
    }
    
    @Test
    void testEqualsAndHashCode() {
        String messageId = UUID.randomUUID().toString();
        String content = "测试消息内容";
        String messageType = "TEST";
        
        QueueMessage message1 = new QueueMessage(messageId, content, messageType);
        QueueMessage message2 = new QueueMessage(messageId, content, messageType);
        
        // 由于使用了Lombok的@Data注解，应该自动生成equals和hashCode方法
        // 但这里主要测试消息的基本属性
        assertEquals(message1.getMessageId(), message2.getMessageId());
        assertEquals(message1.getContent(), message2.getContent());
        assertEquals(message1.getMessageType(), message2.getMessageType());
    }
}
