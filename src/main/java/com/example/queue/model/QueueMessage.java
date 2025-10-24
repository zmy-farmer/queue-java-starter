package com.example.queue.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 队列消息模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueMessage {
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型
     */
    private String messageType;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 优先级
     */
    private Integer priority;
    
    /**
     * 延迟时间（秒）
     */
    private Long delaySeconds;
    
    public QueueMessage(String messageId, String content) {
        this.messageId = messageId;
        this.content = content;
        this.createTime = LocalDateTime.now();
        this.priority = 0;
        this.delaySeconds = 0L;
    }
    
    public QueueMessage(String messageId, String content, String messageType) {
        this.messageId = messageId;
        this.content = content;
        this.messageType = messageType;
        this.createTime = LocalDateTime.now();
        this.priority = 0;
        this.delaySeconds = 0L;
    }
}
