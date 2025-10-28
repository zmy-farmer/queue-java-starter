package com.example.queue.consumer;

import com.example.queue.model.QueueMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;

/**
 * RabbitMQ 消息消费者
 * 演示自动确认和手动确认两种模式
 */
@Slf4j
@Component
public class MessageConsumer {
    
    /**
     * 自动确认模式（当前启用）
     * Spring AMQP 自动处理确认，无需显式调用 basicAck()
     */
    @RabbitListener(queues = "test-queue")
    public void handleMessageAutoAck(QueueMessage message) {
        try {
            log.info("=== 消费者收到消息（自动确认） ===");
            log.info("消息ID: {}", message.getMessageId());
            log.info("消息内容: {}", message.getContent());
            log.info("消息类型: {}", message.getMessageType());
            log.info("创建时间: {}", message.getCreateTime());
            log.info("优先级: {}", message.getPriority());
            log.info("=================================");
            
            // 模拟消息处理
            processMessage(message);
            
            // 方法正常返回 = Spring 自动调用 basicAck()
            // 如果这里抛出异常 = Spring 自动调用 basicNack()
            
        } catch (Exception e) {
            log.error("处理消息失败: {}", e.getMessage(), e);
            // 抛出异常 = Spring 自动拒绝消息
        }
    }
    
    /**
     * 手动确认模式（注释掉，需要时启用）
     * 必须显式调用 basicAck() 或 basicNack()
     */
    /*
    @RabbitListener(queues = "test-queue", ackMode = "MANUAL")
    public void handleMessageManualAck(QueueMessage message, 
                                     Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("=== 消费者收到消息（手动确认） ===");
            log.info("消息ID: {}", message.getMessageId());
            log.info("消息内容: {}", message.getContent());
            log.info("Delivery Tag: {}", deliveryTag);
            log.info("=================================");
            
            // 模拟消息处理
            processMessage(message);
            
            // 手动确认消息 - 必须显式调用
            channel.basicAck(deliveryTag, false);
            log.info("消息确认成功: {}", message.getMessageId());
            
        } catch (Exception e) {
            log.error("处理消息失败: {}", e.getMessage(), e);
            try {
                // 手动拒绝消息，重新入队 - 必须显式调用
                channel.basicNack(deliveryTag, false, true);
                log.warn("消息拒绝，重新入队: {}", message.getMessageId());
            } catch (Exception ackException) {
                log.error("确认消息失败: {}", ackException.getMessage(), ackException);
            }
        }
    }
    */
    
    /**
     * 处理消息的业务逻辑
     */
    private void processMessage(QueueMessage message) {
        try {
            // 模拟业务处理时间
            Thread.sleep(100);
            
            // 根据消息类型进行不同处理
            switch (message.getMessageType()) {
                case "API":
                    log.info("处理API消息: {}", message.getContent());
                    break;
                case "SYSTEM":
                    log.info("处理系统消息: {}", message.getContent());
                    break;
                case "USER":
                    log.info("处理用户消息: {}", message.getContent());
                    break;
                case "CONSUMER_TEST":
                    log.info("处理消费者测试消息: {}", message.getContent());
                    break;
                case "BATCH_TEST":
                    log.info("处理批量测试消息: {}", message.getContent());
                    break;
                default:
                    log.info("处理默认消息: {}", message.getContent());
                    break;
            }
            
            log.info("消息处理完成: {}", message.getMessageId());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("消息处理被中断: {}", message.getMessageId());
        } catch (Exception e) {
            log.error("消息处理异常: {}", e.getMessage(), e);
            throw e; // 重新抛出异常，让上层处理
        }
    }
}