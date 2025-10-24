package com.example.queue.impl;

import com.example.queue.core.AbstractQueueService;
import com.example.queue.model.QueueMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

/**
 * Redis队列实现
 */
@Slf4j
public class RedisQueueService extends AbstractQueueService {
    
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final String queueKey;
    
    public RedisQueueService(String queueName, StringRedisTemplate redisTemplate) {
        super(queueName, "REDIS");
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        this.queueKey = "queue:" + queueName;
        log.info("初始化Redis队列: {}", queueName);
    }
    
    @Override
    public boolean sendMessage(QueueMessage message) {
        try {
            if (message == null) {
                log.warn("消息不能为空");
                return false;
            }
            
            String messageJson = objectMapper.writeValueAsString(message);
            Long result = redisTemplate.opsForList().leftPush(queueKey, messageJson);
            
            if (result != null && result > 0) {
                logOperation("发送消息", "messageId=" + message.getMessageId());
                return true;
            } else {
                log.warn("Redis队列发送失败");
                return false;
            }
        } catch (JsonProcessingException e) {
            log.error("消息序列化失败: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("发送消息失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public QueueMessage receiveMessage() {
        try {
            String messageJson = redisTemplate.opsForList().rightPop(queueKey);
            if (messageJson != null) {
                QueueMessage message = objectMapper.readValue(messageJson, QueueMessage.class);
                logOperation("接收消息", "messageId=" + message.getMessageId());
                return message;
            }
            return null;
        } catch (JsonProcessingException e) {
            log.error("消息反序列化失败: {}", e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("接收消息失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public QueueMessage receiveMessage(long timeoutSeconds) {
        try {
            String messageJson = redisTemplate.opsForList().rightPop(queueKey, Duration.ofSeconds(timeoutSeconds));
            if (messageJson != null) {
                QueueMessage message = objectMapper.readValue(messageJson, QueueMessage.class);
                logOperation("接收消息(超时)", "messageId=" + message.getMessageId(), "timeout=" + timeoutSeconds);
                return message;
            }
            return null;
        } catch (JsonProcessingException e) {
            log.error("消息反序列化失败: {}", e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("接收消息失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public long getQueueSize() {
        try {
            Long size = redisTemplate.opsForList().size(queueKey);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("获取队列大小失败: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    @Override
    public boolean clearQueue() {
        try {
            Boolean result = redisTemplate.delete(queueKey);
            logOperation("清空队列");
            return result != null && result;
        } catch (Exception e) {
            log.error("清空队列失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
