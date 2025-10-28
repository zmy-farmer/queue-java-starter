package com.example.queue.controller;

import com.example.queue.model.QueueMessage;
import com.example.queue.router.QueueRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 消费者测试控制器
 * 用于测试消息消费者功能
 */
@Slf4j
@RestController
@RequestMapping("/api/consumer")
public class ConsumerTestController {
    
    @Autowired
    private QueueRouter queueRouter;
    
    /**
     * 发送测试消息给消费者
     */
    @PostMapping("/send-test")
    public Map<String, Object> sendTestMessage(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String content = (String) request.getOrDefault("content", "测试消息内容");
            String messageType = (String) request.getOrDefault("messageType", "CONSUMER_TEST");
            Integer count = (Integer) request.getOrDefault("count", 1);
            
            log.info("准备发送 {} 条测试消息给消费者", count);
            
            int successCount = 0;
            for (int i = 0; i < count; i++) {
                QueueMessage message = new QueueMessage(
                    UUID.randomUUID().toString(),
                    content + " - 第" + (i + 1) + "条",
                    messageType
                );
                
                boolean sendResult = queueRouter.sendMessage(message);
                if (sendResult) {
                    successCount++;
                }
                
                // 稍微延迟，避免消息发送过快
                Thread.sleep(100);
            }
            
            result.put("success", true);
            result.put("message", "测试消息发送完成");
            result.put("totalCount", count);
            result.put("successCount", successCount);
            result.put("queueType", queueRouter.getCurrentQueueType().getValue());
            result.put("queueName", queueRouter.getCurrentQueueName());
            
            log.info("测试消息发送完成: {}/{}", successCount, count);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "发送测试消息失败: " + e.getMessage());
            log.error("发送测试消息失败", e);
        }
        
        return result;
    }
    
    /**
     * 获取消费者状态信息
     */
    @GetMapping("/status")
    public Map<String, Object> getConsumerStatus() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            result.put("success", true);
            result.put("message", "消费者状态正常");
            result.put("queueType", queueRouter.getCurrentQueueType().getValue());
            result.put("queueName", queueRouter.getCurrentQueueName());
            result.put("queueSize", queueRouter.getQueueSize());
            result.put("isEmpty", queueRouter.isEmpty());
            result.put("consumerActive", true); // 消费者已激活
            result.put("consumerType", "Push模式 - @RabbitListener");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取消费者状态失败: " + e.getMessage());
            log.error("获取消费者状态失败", e);
        }
        
        return result;
    }
}
