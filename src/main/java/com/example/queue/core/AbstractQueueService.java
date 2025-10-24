package com.example.queue.core;

import com.example.queue.model.QueueMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 队列服务抽象类
 */
@Slf4j
public abstract class AbstractQueueService implements QueueService {
    
    protected final String queueName;
    protected final String queueType;
    
    public AbstractQueueService(String queueName, String queueType) {
        this.queueName = queueName;
        this.queueType = queueType;
    }
    
    @Override
    public CompletableFuture<Boolean> sendMessageAsync(QueueMessage message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendMessage(message);
            } catch (Exception e) {
                log.error("异步发送消息失败: {}", e.getMessage(), e);
                return false;
            }
        });
    }
    
    @Override
    public int sendMessages(List<QueueMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return 0;
        }
        
        int successCount = 0;
        for (QueueMessage message : messages) {
            try {
                if (sendMessage(message)) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("批量发送消息失败: {}", e.getMessage(), e);
            }
        }
        
        log.info("批量发送消息完成，成功: {}/{}", successCount, messages.size());
        return successCount;
    }
    
    @Override
    public List<QueueMessage> receiveMessages(int maxMessages) {
        List<QueueMessage> messages = new ArrayList<>();
        for (int i = 0; i < maxMessages; i++) {
            QueueMessage message = receiveMessage();
            if (message == null) {
                break;
            }
            messages.add(message);
        }
        return messages;
    }
    
    @Override
    public boolean isEmpty() {
        return getQueueSize() == 0;
    }
    
    @Override
    public String getQueueType() {
        return queueType;
    }
    
    /**
     * 记录操作日志
     */
    protected void logOperation(String operation, Object... params) {
        log.info("队列[{}] {}: {}", queueName, operation, String.format("%s", params));
    }
}
