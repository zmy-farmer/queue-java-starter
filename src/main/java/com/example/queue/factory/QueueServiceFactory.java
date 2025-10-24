package com.example.queue.factory;

import com.example.queue.core.QueueService;
import com.example.queue.core.QueueType;
import com.example.queue.impl.JavaQueueService;
import com.example.queue.impl.RabbitMQQueueService;
import com.example.queue.impl.RedisQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 队列服务工厂
 */
@Slf4j
public class QueueServiceFactory {
    
    private final StringRedisTemplate redisTemplate;
    private final RabbitTemplate rabbitTemplate;
    
    public QueueServiceFactory(StringRedisTemplate redisTemplate, RabbitTemplate rabbitTemplate) {
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }
    
    /**
     * 创建队列服务
     * @param queueName 队列名称
     * @param queueType 队列类型
     * @return 队列服务实例
     */
    public QueueService createQueueService(String queueName, QueueType queueType) {
        if (queueName == null || queueName.trim().isEmpty()) {
            throw new IllegalArgumentException("队列名称不能为空");
        }
        
        log.info("创建队列服务: name={}, type={}", queueName, queueType);
        
        switch (queueType) {
            case JAVA:
                return new JavaQueueService(queueName);
                
            case REDIS:
                if (redisTemplate == null) {
                    throw new IllegalStateException("Redis模板未配置，无法创建Redis队列");
                }
                return new RedisQueueService(queueName, redisTemplate);
                
            case RABBITMQ:
                if (rabbitTemplate == null) {
                    throw new IllegalStateException("RabbitMQ模板未配置，无法创建RabbitMQ队列");
                }
                return new RabbitMQQueueService(queueName, rabbitTemplate);
                
            default:
                log.warn("未知的队列类型: {}，使用默认Java队列", queueType);
                return new JavaQueueService(queueName);
        }
    }
    
    /**
     * 创建队列服务（使用字符串类型）
     * @param queueName 队列名称
     * @param queueTypeString 队列类型字符串
     * @return 队列服务实例
     */
    public QueueService createQueueService(String queueName, String queueTypeString) {
        QueueType queueType = QueueType.fromString(queueTypeString);
        return createQueueService(queueName, queueType);
    }
}
