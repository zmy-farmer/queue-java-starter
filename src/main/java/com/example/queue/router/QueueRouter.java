package com.example.queue.router;

import com.example.queue.core.QueueService;
import com.example.queue.core.QueueType;
import com.example.queue.factory.QueueServiceFactory;
import com.example.queue.model.QueueMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 队列路由器
 * 支持动态切换不同的队列实现
 */
@Slf4j
public class QueueRouter {
    
    private final QueueServiceFactory queueServiceFactory;
    private final Map<String, QueueService> queueServices;
    private QueueType currentQueueType;
    private String currentQueueName;
    
    public QueueRouter(QueueServiceFactory queueServiceFactory) {
        this.queueServiceFactory = queueServiceFactory;
        this.queueServices = new ConcurrentHashMap<>();
        this.currentQueueType = QueueType.JAVA; // 默认使用Java队列
        this.currentQueueName = "default";
    }
    
    /**
     * 切换到指定的队列类型
     * @param queueType 队列类型
     * @param queueName 队列名称
     */
    public void switchQueue(QueueType queueType, String queueName) {
        if (queueType == null || queueName == null || queueName.trim().isEmpty()) {
            throw new IllegalArgumentException("队列类型和名称不能为空");
        }
        
        String queueKey = queueType.getValue() + ":" + queueName;
        
        // 如果队列服务不存在，创建新的
        if (!queueServices.containsKey(queueKey)) {
            QueueService queueService = queueServiceFactory.createQueueService(queueName, queueType);
            queueServices.put(queueKey, queueService);
        }
        
        this.currentQueueType = queueType;
        this.currentQueueName = queueName;
        
        log.info("切换到队列: type={}, name={}", queueType, queueName);
    }
    
    /**
     * 切换到指定的队列类型（使用字符串）
     * @param queueTypeString 队列类型字符串
     * @param queueName 队列名称
     */
    public void switchQueue(String queueTypeString, String queueName) {
        QueueType queueType = QueueType.fromString(queueTypeString);
        switchQueue(queueType, queueName);
    }
    
    /**
     * 获取当前队列服务
     * @return 当前队列服务
     */
    private QueueService getCurrentQueueService() {
        String queueKey = currentQueueType.getValue() + ":" + currentQueueName;
        return queueServices.get(queueKey);
    }
    
    /**
     * 发送消息到当前队列
     * @param message 消息
     * @return 是否发送成功
     */
    public boolean sendMessage(QueueMessage message) {
        QueueService queueService = getCurrentQueueService();
        if (queueService == null) {
            log.error("当前队列服务不存在");
            return false;
        }
        
        log.info("通过{}队列发送消息: {}", currentQueueType, message.getMessageId());
        return queueService.sendMessage(message);
    }
    
    /**
     * 异步发送消息到当前队列
     * @param message 消息
     * @return 异步结果
     */
    public CompletableFuture<Boolean> sendMessageAsync(QueueMessage message) {
        QueueService queueService = getCurrentQueueService();
        if (queueService == null) {
            log.error("当前队列服务不存在");
            return CompletableFuture.completedFuture(false);
        }
        
        log.info("通过{}队列异步发送消息: {}", currentQueueType, message.getMessageId());
        return queueService.sendMessageAsync(message);
    }
    
    /**
     * 批量发送消息到当前队列
     * @param messages 消息列表
     * @return 发送成功的数量
     */
    public int sendMessages(List<QueueMessage> messages) {
        QueueService queueService = getCurrentQueueService();
        if (queueService == null) {
            log.error("当前队列服务不存在");
            return 0;
        }
        
        log.info("通过{}队列批量发送消息: {}条", currentQueueType, messages.size());
        return queueService.sendMessages(messages);
    }
    
    /**
     * 从当前队列接收消息
     * @return 消息
     */
    public QueueMessage receiveMessage() {
        QueueService queueService = getCurrentQueueService();
        if (queueService == null) {
            log.error("当前队列服务不存在");
            return null;
        }
        
        QueueMessage message = queueService.receiveMessage();
        if (message != null) {
            log.info("从{}队列接收消息: {}", currentQueueType, message.getMessageId());
        }
        return message;
    }
    
    /**
     * 从当前队列接收消息（带超时）
     * @param timeoutSeconds 超时时间
     * @return 消息
     */
    public QueueMessage receiveMessage(long timeoutSeconds) {
        QueueService queueService = getCurrentQueueService();
        if (queueService == null) {
            log.error("当前队列服务不存在");
            return null;
        }
        
        QueueMessage message = queueService.receiveMessage(timeoutSeconds);
        if (message != null) {
            log.info("从{}队列接收消息(超时): {}", currentQueueType, message.getMessageId());
        }
        return message;
    }
    
    /**
     * 从当前队列批量接收消息
     * @param maxMessages 最大消息数量
     * @return 消息列表
     */
    public List<QueueMessage> receiveMessages(int maxMessages) {
        QueueService queueService = getCurrentQueueService();
        if (queueService == null) {
            log.error("当前队列服务不存在");
            return List.of();
        }
        
        List<QueueMessage> messages = queueService.receiveMessages(maxMessages);
        log.info("从{}队列批量接收消息: {}条", currentQueueType, messages.size());
        return messages;
    }
    
    /**
     * 获取当前队列大小
     * @return 队列大小
     */
    public long getQueueSize() {
        QueueService queueService = getCurrentQueueService();
        if (queueService == null) {
            log.error("当前队列服务不存在");
            return 0;
        }
        
        return queueService.getQueueSize();
    }
    
    /**
     * 清空当前队列
     * @return 是否清空成功
     */
    public boolean clearQueue() {
        QueueService queueService = getCurrentQueueService();
        if (queueService == null) {
            log.error("当前队列服务不存在");
            return false;
        }
        
        log.info("清空{}队列", currentQueueType);
        return queueService.clearQueue();
    }
    
    /**
     * 检查当前队列是否为空
     * @return 是否为空
     */
    public boolean isEmpty() {
        QueueService queueService = getCurrentQueueService();
        if (queueService == null) {
            log.error("当前队列服务不存在");
            return true;
        }
        
        return queueService.isEmpty();
    }
    
    /**
     * 获取当前队列类型
     * @return 当前队列类型
     */
    public QueueType getCurrentQueueType() {
        return currentQueueType;
    }
    
    /**
     * 获取当前队列名称
     * @return 当前队列名称
     */
    public String getCurrentQueueName() {
        return currentQueueName;
    }
    
    /**
     * 获取所有已创建的队列服务
     * @return 队列服务映射
     */
    public Map<String, QueueService> getAllQueueServices() {
        return Map.copyOf(queueServices);
    }
}
