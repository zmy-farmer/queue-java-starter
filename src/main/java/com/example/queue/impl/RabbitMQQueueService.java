package com.example.queue.impl;

import com.example.queue.core.AbstractQueueService;
import com.example.queue.model.QueueMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * RabbitMQ队列实现
 */
@Slf4j
public class RabbitMQQueueService extends AbstractQueueService {
    
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final String exchangeName;
    private final String routingKey;
    
    public RabbitMQQueueService(String queueName, RabbitTemplate rabbitTemplate) {
        super(queueName, "RABBITMQ");
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.exchangeName = "queue.exchange";
        this.routingKey = queueName;
        log.info("初始化RabbitMQ队列: {}", queueName);
    }
    
    @Override
    public boolean sendMessage(QueueMessage message) {
        try {
            if (message == null) {
                log.warn("消息不能为空");
                return false;
            }
            
            String messageJson = objectMapper.writeValueAsString(message);
            
            // 设置消息属性
            MessageProperties properties = new MessageProperties();
            properties.setContentType("application/json");
            properties.setMessageId(message.getMessageId());
            if (message.getPriority() != null) {
                properties.setPriority(message.getPriority());
            }
            
            Message amqpMessage = new Message(messageJson.getBytes(), properties);
            
            rabbitTemplate.send(exchangeName, routingKey, amqpMessage);
            logOperation("发送消息", "messageId=" + message.getMessageId());
            return true;
            
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
            Message message = rabbitTemplate.receive(queueName, 1000); // 1秒超时
            if (message != null) {
                String messageJson = new String(message.getBody());
                QueueMessage queueMessage = objectMapper.readValue(messageJson, QueueMessage.class);
                logOperation("接收消息", "messageId=" + queueMessage.getMessageId());
                return queueMessage;
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
            Message message = rabbitTemplate.receive(queueName, timeoutSeconds * 1000);
            if (message != null) {
                String messageJson = new String(message.getBody());
                QueueMessage queueMessage = objectMapper.readValue(messageJson, QueueMessage.class);
                logOperation("接收消息(超时)", "messageId=" + queueMessage.getMessageId(), "timeout=" + timeoutSeconds);
                return queueMessage;
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
            // RabbitMQ没有直接获取队列大小的方法，这里返回0
            // 实际项目中可以通过管理API获取
            log.warn("RabbitMQ不支持直接获取队列大小");
            return 0;
        } catch (Exception e) {
            log.error("获取队列大小失败: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    @Override
    public boolean clearQueue() {
        try {
            // RabbitMQ清空队列需要管理API，这里只是记录日志
            log.warn("RabbitMQ不支持直接清空队列，需要管理API");
            logOperation("清空队列");
            return true;
        } catch (Exception e) {
            log.error("清空队列失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
