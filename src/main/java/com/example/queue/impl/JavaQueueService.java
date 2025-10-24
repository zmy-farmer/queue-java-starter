package com.example.queue.impl;

import com.example.queue.core.AbstractQueueService;
import com.example.queue.model.QueueMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Java内置队列实现
 */
@Slf4j
public class JavaQueueService extends AbstractQueueService {
    
    private final BlockingQueue<QueueMessage> queue;
    
    public JavaQueueService(String queueName) {
        super(queueName, "JAVA");
        this.queue = new LinkedBlockingQueue<>();
        log.info("初始化Java队列: {}", queueName);
    }
    
    @Override
    public boolean sendMessage(QueueMessage message) {
        try {
            if (message == null) {
                log.warn("消息不能为空");
                return false;
            }
            
            boolean result = queue.offer(message);
            if (result) {
                logOperation("发送消息", "messageId=" + message.getMessageId());
            } else {
                log.warn("队列已满，无法添加消息");
            }
            return result;
        } catch (Exception e) {
            log.error("发送消息失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public QueueMessage receiveMessage() {
        try {
            QueueMessage message = queue.poll();
            if (message != null) {
                logOperation("接收消息", "messageId=" + message.getMessageId());
            }
            return message;
        } catch (Exception e) {
            log.error("接收消息失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public QueueMessage receiveMessage(long timeoutSeconds) {
        try {
            QueueMessage message = queue.poll(timeoutSeconds, TimeUnit.SECONDS);
            if (message != null) {
                logOperation("接收消息(超时)", "messageId=" + message.getMessageId(), "timeout=" + timeoutSeconds);
            }
            return message;
        } catch (InterruptedException e) {
            log.warn("接收消息被中断: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            log.error("接收消息失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public long getQueueSize() {
        return queue.size();
    }
    
    @Override
    public boolean clearQueue() {
        try {
            queue.clear();
            logOperation("清空队列");
            return true;
        } catch (Exception e) {
            log.error("清空队列失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
