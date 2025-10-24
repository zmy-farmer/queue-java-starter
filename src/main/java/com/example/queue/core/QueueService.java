package com.example.queue.core;

import com.example.queue.model.QueueMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 队列服务接口
 */
public interface QueueService {
    
    /**
     * 发送消息到队列
     * @param message 消息
     * @return 是否发送成功
     */
    boolean sendMessage(QueueMessage message);
    
    /**
     * 异步发送消息到队列
     * @param message 消息
     * @return 异步结果
     */
    CompletableFuture<Boolean> sendMessageAsync(QueueMessage message);
    
    /**
     * 批量发送消息
     * @param messages 消息列表
     * @return 发送成功的数量
     */
    int sendMessages(List<QueueMessage> messages);
    
    /**
     * 接收消息
     * @return 消息，如果没有消息返回null
     */
    QueueMessage receiveMessage();
    
    /**
     * 接收消息（带超时）
     * @param timeoutSeconds 超时时间（秒）
     * @return 消息，如果没有消息返回null
     */
    QueueMessage receiveMessage(long timeoutSeconds);
    
    /**
     * 批量接收消息
     * @param maxMessages 最大消息数量
     * @return 消息列表
     */
    List<QueueMessage> receiveMessages(int maxMessages);
    
    /**
     * 获取队列大小
     * @return 队列中消息数量
     */
    long getQueueSize();
    
    /**
     * 清空队列
     * @return 是否清空成功
     */
    boolean clearQueue();
    
    /**
     * 检查队列是否为空
     * @return 是否为空
     */
    boolean isEmpty();
    
    /**
     * 获取队列类型
     * @return 队列类型
     */
    String getQueueType();
}
