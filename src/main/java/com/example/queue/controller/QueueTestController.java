package com.example.queue.controller;

import com.example.queue.model.QueueMessage;
import com.example.queue.router.QueueRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 队列测试控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/queue")
public class QueueTestController {
    
    @Autowired
    private QueueRouter queueRouter;
    
    /**
     * 切换队列类型
     */
    @PostMapping("/switch")
    public Map<String, Object> switchQueue(@RequestParam String queueType, 
                                         @RequestParam String queueName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            queueRouter.switchQueue(queueType, queueName);
            result.put("success", true);
            result.put("message", "队列切换成功");
            result.put("currentQueueType", queueRouter.getCurrentQueueType().getValue());
            result.put("currentQueueName", queueRouter.getCurrentQueueName());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "队列切换失败: " + e.getMessage());
            log.error("队列切换失败", e);
        }
        
        return result;
    }
    
    /**
     * 发送消息
     */
    @PostMapping("/send")
    public Map<String, Object> sendMessage(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String content = (String) request.get("content");
            String messageType = (String) request.getOrDefault("messageType", "API");
            
            QueueMessage message = new QueueMessage(
                UUID.randomUUID().toString(),
                content,
                messageType
            );
            
            boolean sendResult = queueRouter.sendMessage(message);
            
            result.put("success", sendResult);
            result.put("message", sendResult ? "消息发送成功" : "消息发送失败");
            result.put("messageId", message.getMessageId());
            result.put("queueType", queueRouter.getCurrentQueueType().getValue());
            result.put("queueName", queueRouter.getCurrentQueueName());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "消息发送失败: " + e.getMessage());
            log.error("消息发送失败", e);
        }
        
        return result;
    }
    
    /**
     * 接收消息
     */
    @GetMapping("/receive")
    public Map<String, Object> receiveMessage() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            QueueMessage message = queueRouter.receiveMessage();
            
            if (message != null) {
                result.put("success", true);
                result.put("message", "消息接收成功");
                result.put("data", message);
            } else {
                result.put("success", false);
                result.put("message", "队列中没有消息");
            }
            
            result.put("queueType", queueRouter.getCurrentQueueType().getValue());
            result.put("queueName", queueRouter.getCurrentQueueName());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "消息接收失败: " + e.getMessage());
            log.error("消息接收失败", e);
        }
        
        return result;
    }
    
    /**
     * 批量接收消息
     */
    @GetMapping("/receive/batch")
    public Map<String, Object> receiveMessages(@RequestParam(defaultValue = "10") int maxMessages) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<QueueMessage> messages = queueRouter.receiveMessages(maxMessages);
            
            result.put("success", true);
            result.put("message", "批量接收消息成功");
            result.put("data", messages);
            result.put("count", messages.size());
            result.put("queueType", queueRouter.getCurrentQueueType().getValue());
            result.put("queueName", queueRouter.getCurrentQueueName());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量接收消息失败: " + e.getMessage());
            log.error("批量接收消息失败", e);
        }
        
        return result;
    }
    
    /**
     * 获取队列信息
     */
    @GetMapping("/info")
    public Map<String, Object> getQueueInfo() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            result.put("success", true);
            result.put("queueType", queueRouter.getCurrentQueueType().getValue());
            result.put("queueName", queueRouter.getCurrentQueueName());
            result.put("queueSize", queueRouter.getQueueSize());
            result.put("isEmpty", queueRouter.isEmpty());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取队列信息失败: " + e.getMessage());
            log.error("获取队列信息失败", e);
        }
        
        return result;
    }
    
    /**
     * 清空队列
     */
    @PostMapping("/clear")
    public Map<String, Object> clearQueue() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean clearResult = queueRouter.clearQueue();
            
            result.put("success", clearResult);
            result.put("message", clearResult ? "队列清空成功" : "队列清空失败");
            result.put("queueType", queueRouter.getCurrentQueueType().getValue());
            result.put("queueName", queueRouter.getCurrentQueueName());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "队列清空失败: " + e.getMessage());
            log.error("队列清空失败", e);
        }
        
        return result;
    }
    
    /**
     * 获取支持的队列类型
     */
    @GetMapping("/types")
    public Map<String, Object> getSupportedQueueTypes() {
        Map<String, Object> result = new HashMap<>();
        
        result.put("success", true);
        result.put("supportedTypes", new String[]{"java", "redis", "rabbitmq"});
        result.put("currentType", queueRouter.getCurrentQueueType().getValue());
        result.put("currentName", queueRouter.getCurrentQueueName());
        
        return result;
    }
}
