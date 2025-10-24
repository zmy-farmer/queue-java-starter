package com.example.queue;

import com.example.queue.core.QueueType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 队列类型测试
 */
class QueueTypeTest {
    
    @Test
    void testFromString() {
        // 测试有效的队列类型
        assertEquals(QueueType.JAVA, QueueType.fromString("java"));
        assertEquals(QueueType.JAVA, QueueType.fromString("JAVA"));
        assertEquals(QueueType.JAVA, QueueType.fromString("Java"));
        
        assertEquals(QueueType.REDIS, QueueType.fromString("redis"));
        assertEquals(QueueType.REDIS, QueueType.fromString("REDIS"));
        assertEquals(QueueType.REDIS, QueueType.fromString("Redis"));
        
        assertEquals(QueueType.RABBITMQ, QueueType.fromString("rabbitmq"));
        assertEquals(QueueType.RABBITMQ, QueueType.fromString("RABBITMQ"));
        assertEquals(QueueType.RABBITMQ, QueueType.fromString("RabbitMQ"));
    }
    
    @Test
    void testFromStringWithNull() {
        // 测试null值，应该返回默认的JAVA类型
        assertEquals(QueueType.JAVA, QueueType.fromString(null));
    }
    
    @Test
    void testFromStringWithInvalidValue() {
        // 测试无效值，应该返回默认的JAVA类型
        assertEquals(QueueType.JAVA, QueueType.fromString("invalid"));
        assertEquals(QueueType.JAVA, QueueType.fromString(""));
        assertEquals(QueueType.JAVA, QueueType.fromString("unknown"));
    }
    
    @Test
    void testGetValue() {
        // 测试获取值
        assertEquals("java", QueueType.JAVA.getValue());
        assertEquals("redis", QueueType.REDIS.getValue());
        assertEquals("rabbitmq", QueueType.RABBITMQ.getValue());
    }
}
