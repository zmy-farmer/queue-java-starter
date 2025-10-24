package com.example.queue.config;

import com.example.queue.factory.QueueServiceFactory;
import com.example.queue.router.QueueRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * 队列配置类
 */
@Configuration
public class QueueConfig {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 队列服务工厂
     */
    @Bean
    public QueueServiceFactory queueServiceFactory() {
        return new QueueServiceFactory(redisTemplate, rabbitTemplate);
    }
    
    /**
     * 队列路由器
     */
    @Bean
    public QueueRouter queueRouter(QueueServiceFactory queueServiceFactory) {
        return new QueueRouter(queueServiceFactory);
    }
}
