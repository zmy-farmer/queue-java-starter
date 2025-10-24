package com.example.queue.core;

/**
 * 队列类型枚举
 */
public enum QueueType {
    
    /**
     * Java内置队列
     */
    JAVA("java"),
    
    /**
     * Redis队列
     */
    REDIS("redis"),
    
    /**
     * RabbitMQ队列
     */
    RABBITMQ("rabbitmq");
    
    private final String value;
    
    QueueType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * 根据字符串获取队列类型
     */
    public static QueueType fromString(String value) {
        if (value == null) {
            return JAVA; // 默认使用Java队列
        }
        
        for (QueueType type : QueueType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        
        return JAVA; // 默认使用Java队列
    }
}
