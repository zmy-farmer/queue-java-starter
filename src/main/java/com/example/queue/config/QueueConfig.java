package com.example.queue.config;

import com.example.queue.factory.QueueServiceFactory;
import com.example.queue.router.QueueRouter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 队列配置类
 */
@Configuration
public class QueueConfig {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    /**
     * 队列服务工厂
     */
    @Bean
    public QueueServiceFactory queueServiceFactory(RabbitTemplate rabbitTemplate) {
        return new QueueServiceFactory(redisTemplate, rabbitTemplate);
    }
    
    /**
     * 队列路由器
     */
    @Bean
    public QueueRouter queueRouter(QueueServiceFactory queueServiceFactory) {
        return new QueueRouter(queueServiceFactory);
    }
    
    /**
     * RabbitMQ 交换机配置
     */
    @Bean
    public DirectExchange queueExchange() {
        return new DirectExchange("queue.exchange", true, false);
    }
    
    /**
     * RabbitMQ 队列配置（动态创建）
     */
    @Bean
    public Queue testQueue() {
        return QueueBuilder.durable("test-queue").build();
    }
    
    /**
     * RabbitMQ 绑定配置
     */
    @Bean
    public Binding queueBinding() {
        return BindingBuilder.bind(testQueue()).to(queueExchange()).with("test-queue");
    }
    
    /**
     * JSON 消息转换器
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        // 配置 ObjectMapper 支持 Java 8 时间类型
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 创建消息转换器并设置 ObjectMapper
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        return converter;
    }
    
    /**
     * 配置 RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
