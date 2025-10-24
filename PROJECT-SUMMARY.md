# 队列路由器测试项目总结

## 项目概述

本项目实现了一个支持多种队列（Java内置队列、Redis队列、RabbitMQ队列）的测试系统，使用路由设计模式实现队列的动态切换。

## 核心特性

### 1. 多队列支持
- **Java内置队列**: 基于 `LinkedBlockingQueue` 实现，无需外部依赖
- **Redis队列**: 基于Redis List实现，支持分布式部署
- **RabbitMQ队列**: 基于RabbitMQ实现，支持消息持久化

### 2. 设计模式应用
- **路由模式**: `QueueRouter` 类实现队列动态切换
- **工厂模式**: `QueueServiceFactory` 类负责创建不同类型的队列服务
- **策略模式**: 不同队列实现都实现了 `QueueService` 接口

### 3. 完整功能
- 消息发送/接收
- 批量操作
- 队列状态监控
- 队列切换
- 异步操作支持

## 项目结构

```
src/
├── main/java/com/example/queue/
│   ├── QueueRouterApplication.java          # 应用程序入口
│   ├── config/QueueConfig.java              # 队列配置
│   ├── controller/QueueTestController.java  # REST API控制器
│   ├── core/                                # 核心接口和抽象类
│   │   ├── QueueService.java               # 队列服务接口
│   │   ├── AbstractQueueService.java      # 队列服务抽象类
│   │   ├── QueueType.java                  # 队列类型枚举
│   │   └── QueueMessage.java              # 队列消息模型
│   ├── factory/QueueServiceFactory.java   # 队列服务工厂
│   ├── router/QueueRouter.java             # 队列路由器
│   ├── impl/                               # 队列实现
│   │   ├── JavaQueueService.java          # Java内置队列
│   │   ├── RedisQueueService.java         # Redis队列
│   │   └── RabbitMQQueueService.java      # RabbitMQ队列
│   └── example/QueueExample.java          # 使用示例
└── test/java/com/example/queue/            # 测试代码
    ├── QueueRouterTest.java               # 队列路由器测试
    ├── JavaQueueServiceTest.java          # Java队列测试
    ├── QueueTypeTest.java                 # 队列类型测试
    └── QueueMessageTest.java              # 队列消息测试
```

## 技术栈

- **Java 11+**
- **Spring Boot 2.7.18**
- **Spring Data Redis**
- **Spring AMQP (RabbitMQ)**
- **Jackson (JSON处理)**
- **Lombok**
- **JUnit 5**
- **Mockito**

## 核心组件

### 1. QueueService接口
定义队列操作的标准接口：
```java
public interface QueueService {
    boolean sendMessage(QueueMessage message);
    QueueMessage receiveMessage();
    long getQueueSize();
    boolean clearQueue();
    // ... 其他方法
}
```

### 2. QueueRouter路由器
实现队列动态切换：
```java
public class QueueRouter {
    public void switchQueue(QueueType queueType, String queueName) {
        // 动态切换队列实现
    }
}
```

### 3. QueueServiceFactory工厂
负责创建不同类型的队列服务：
```java
public class QueueServiceFactory {
    public QueueService createQueueService(String queueName, QueueType queueType) {
        // 根据类型创建对应的队列服务
    }
}
```

## API接口

### 队列操作
- `POST /api/queue/switch` - 切换队列类型
- `POST /api/queue/send` - 发送消息
- `GET /api/queue/receive` - 接收消息
- `GET /api/queue/receive/batch` - 批量接收消息
- `GET /api/queue/info` - 获取队列信息
- `POST /api/queue/clear` - 清空队列
- `GET /api/queue/types` - 获取支持的队列类型

### 使用示例

#### 1. 切换队列
```bash
curl -X POST "http://localhost:8080/api/queue/switch?queueType=java&queueName=test-queue"
```

#### 2. 发送消息
```bash
curl -X POST "http://localhost:8080/api/queue/send" \
  -H "Content-Type: application/json" \
  -d '{"content":"测试消息","messageType":"TEST"}'
```

#### 3. 接收消息
```bash
curl -X GET "http://localhost:8080/api/queue/receive"
```

## 测试覆盖

### 单元测试
- `QueueRouterTest`: 队列路由器功能测试
- `JavaQueueServiceTest`: Java队列服务测试
- `QueueTypeTest`: 队列类型枚举测试
- `QueueMessageTest`: 队列消息模型测试

### 集成测试
- REST API接口测试
- 队列切换功能测试
- 消息发送/接收测试
- 批量操作测试

## 部署方式

### 1. 本地开发
```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 启动应用
mvn spring-boot:run
```

### 2. Docker部署
```bash
# 使用Docker Compose启动所有服务
docker-compose up -d

# 访问应用
http://localhost:8080
```

### 3. 生产部署
```bash
# 构建应用
mvn clean package

# 运行应用
java -jar target/queue-router-1.0.0.jar
```

## 配置说明

### application.yml
```yaml
spring:
  redis:
    host: localhost
    port: 6379
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

queue:
  default-type: java
  default-name: default-queue
  router:
    enabled: true
    max-queue-services: 10
```

## 扩展性

### 添加新队列类型
1. 创建新的队列服务实现类
2. 在 `QueueType` 枚举中添加新类型
3. 在 `QueueServiceFactory` 中添加创建逻辑
4. 更新相关测试

### 自定义配置
可以通过配置文件自定义队列行为：
```yaml
queue:
  custom:
    max-size: 1000
    timeout: 5000
    retry-count: 3
```

## 性能特点

### Java队列
- 内存操作，性能最高
- 适合单机应用
- 不支持持久化

### Redis队列
- 网络操作，性能中等
- 支持分布式部署
- 支持持久化

### RabbitMQ队列
- 网络操作，性能中等
- 支持消息持久化
- 支持复杂路由规则

## 使用场景

### 1. 开发测试
- 使用Java队列进行快速测试
- 无需外部依赖

### 2. 生产环境
- 使用Redis队列支持分布式部署
- 使用RabbitMQ队列支持消息持久化

### 3. 混合部署
- 根据业务需求动态切换队列类型
- 支持A/B测试

## 监控和运维

### 日志配置
```yaml
logging:
  level:
    com.example.queue: DEBUG
    org.springframework.amqp: INFO
    org.springframework.data.redis: INFO
```

### 健康检查
- 队列状态监控
- 连接状态检查
- 性能指标统计

## 总结

本项目成功实现了一个支持多种队列的路由系统，具有以下优势：

1. **灵活性**: 支持动态切换队列类型
2. **可扩展性**: 易于添加新的队列实现
3. **可测试性**: 完整的单元测试和集成测试
4. **易用性**: 提供REST API和代码示例
5. **生产就绪**: 支持Docker部署和配置管理

该项目展示了设计模式在实际项目中的应用，为队列系统的设计和实现提供了很好的参考。
