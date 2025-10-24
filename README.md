# 队列路由器测试项目

这是一个支持多种队列（Java内置队列、Redis队列、RabbitMQ队列）的测试项目，使用路由设计模式实现队列的动态切换。

## 项目特性

- 🚀 **多队列支持**: 支持Java内置队列、Redis队列、RabbitMQ队列
- 🔄 **路由模式**: 使用路由设计模式实现队列动态切换
- 🏭 **工厂模式**: 使用工厂模式创建不同类型的队列服务
- 🧪 **完整测试**: 包含完整的单元测试和集成测试
- 🌐 **REST API**: 提供RESTful API进行队列操作测试
- 📊 **监控支持**: 支持队列状态监控和统计

## 技术栈

- **Java 11+**
- **Spring Boot 2.7.18**
- **Spring Data Redis**
- **Spring AMQP (RabbitMQ)**
- **Jackson (JSON处理)**
- **Lombok**
- **JUnit 5**
- **Mockito**

## 项目结构

```
src/
├── main/java/com/example/queue/
│   ├── QueueRouterApplication.java          # 应用程序入口
│   ├── config/
│   │   └── QueueConfig.java                # 队列配置
│   ├── controller/
│   │   └── QueueTestController.java        # REST API控制器
│   ├── core/
│   │   ├── QueueService.java               # 队列服务接口
│   │   ├── AbstractQueueService.java      # 队列服务抽象类
│   │   ├── QueueType.java                 # 队列类型枚举
│   │   └── QueueMessage.java              # 队列消息模型
│   ├── factory/
│   │   └── QueueServiceFactory.java       # 队列服务工厂
│   ├── router/
│   │   └── QueueRouter.java               # 队列路由器
│   └── impl/
│       ├── JavaQueueService.java          # Java内置队列实现
│       ├── RedisQueueService.java         # Redis队列实现
│       └── RabbitMQQueueService.java      # RabbitMQ队列实现
└── test/java/com/example/queue/
    ├── QueueRouterTest.java               # 队列路由器测试
    ├── JavaQueueServiceTest.java          # Java队列测试
    ├── QueueTypeTest.java                 # 队列类型测试
    └── QueueMessageTest.java              # 队列消息测试
```

## 快速开始

### 1. 环境准备

确保你的系统已安装：
- Java 11+
- Maven 3.6+
- Redis (可选，用于Redis队列测试)
- RabbitMQ (可选，用于RabbitMQ队列测试)

### 2. 克隆项目

```bash
git clone <repository-url>
cd queue-router
```

### 3. 编译项目

```bash
mvn clean compile
```

### 4. 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试
mvn test -Dtest=QueueRouterTest
```

### 5. 启动应用

```bash
mvn spring-boot:run
```

应用启动后，访问 `http://localhost:8080` 进行测试。

## 使用说明

### 队列类型

项目支持三种队列类型：

1. **Java内置队列** (`java`)
   - 基于 `LinkedBlockingQueue` 实现
   - 无需外部依赖
   - 适合单机应用

2. **Redis队列** (`redis`)
   - 基于Redis List实现
   - 支持分布式部署
   - 需要Redis服务器

3. **RabbitMQ队列** (`rabbitmq`)
   - 基于RabbitMQ实现
   - 支持消息持久化
   - 需要RabbitMQ服务器

### API接口

#### 1. 切换队列类型

```http
POST /api/queue/switch?queueType=java&queueName=test-queue
```

#### 2. 发送消息

```http
POST /api/queue/send
Content-Type: application/json

{
  "content": "测试消息内容",
  "messageType": "TEST"
}
```

#### 3. 接收消息

```http
GET /api/queue/receive
```

#### 4. 批量接收消息

```http
GET /api/queue/receive/batch?maxMessages=10
```

#### 5. 获取队列信息

```http
GET /api/queue/info
```

#### 6. 清空队列

```http
POST /api/queue/clear
```

#### 7. 获取支持的队列类型

```http
GET /api/queue/types
```

### 代码示例

#### 基本使用

```java
@Autowired
private QueueRouter queueRouter;

// 切换到Java队列
queueRouter.switchQueue(QueueType.JAVA, "test-queue");

// 发送消息
QueueMessage message = new QueueMessage("msg-001", "Hello World", "TEST");
boolean success = queueRouter.sendMessage(message);

// 接收消息
QueueMessage receivedMessage = queueRouter.receiveMessage();
```

#### 队列切换

```java
// 切换到Redis队列
queueRouter.switchQueue(QueueType.REDIS, "redis-queue");

// 切换到RabbitMQ队列
queueRouter.switchQueue(QueueType.RABBITMQ, "rabbitmq-queue");
```

#### 批量操作

```java
// 批量发送消息
List<QueueMessage> messages = Arrays.asList(
    new QueueMessage("msg-001", "消息1", "BATCH"),
    new QueueMessage("msg-002", "消息2", "BATCH")
);
int sentCount = queueRouter.sendMessages(messages);

// 批量接收消息
List<QueueMessage> receivedMessages = queueRouter.receiveMessages(10);
```

## 配置说明

### application.yml

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
  
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

## 测试说明

### 单元测试

项目包含完整的单元测试：

- `QueueRouterTest`: 队列路由器测试
- `JavaQueueServiceTest`: Java队列服务测试
- `QueueTypeTest`: 队列类型测试
- `QueueMessageTest`: 队列消息测试

### 集成测试

可以通过REST API进行集成测试：

1. 启动应用
2. 使用Postman或curl测试API接口
3. 验证不同队列类型的功能

## 设计模式

### 1. 路由模式 (Router Pattern)

`QueueRouter` 类实现了路由模式，允许动态切换不同的队列实现：

```java
public class QueueRouter {
    public void switchQueue(QueueType queueType, String queueName) {
        // 动态切换队列实现
    }
}
```

### 2. 工厂模式 (Factory Pattern)

`QueueServiceFactory` 类实现了工厂模式，负责创建不同类型的队列服务：

```java
public class QueueServiceFactory {
    public QueueService createQueueService(String queueName, QueueType queueType) {
        // 根据类型创建对应的队列服务
    }
}
```

### 3. 策略模式 (Strategy Pattern)

不同的队列实现都实现了 `QueueService` 接口，可以动态替换：

```java
public interface QueueService {
    boolean sendMessage(QueueMessage message);
    QueueMessage receiveMessage();
    // ... 其他方法
}
```

## 扩展说明

### 添加新的队列类型

1. 创建新的队列服务实现类
2. 在 `QueueType` 枚举中添加新类型
3. 在 `QueueServiceFactory` 中添加创建逻辑
4. 更新相关测试

### 自定义队列配置

可以通过配置文件自定义队列行为：

```yaml
queue:
  custom:
    max-size: 1000
    timeout: 5000
    retry-count: 3
```

## 故障排除

### 常见问题

1. **Redis连接失败**
   - 检查Redis服务是否启动
   - 验证连接配置

2. **RabbitMQ连接失败**
   - 检查RabbitMQ服务是否启动
   - 验证用户名密码配置

3. **队列切换失败**
   - 检查队列名称是否有效
   - 验证队列类型是否支持

### 日志配置

```yaml
logging:
  level:
    com.example.queue: DEBUG
    org.springframework.amqp: INFO
    org.springframework.data.redis: INFO
```

## 贡献指南

1. Fork 项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

MIT License

## 联系方式

如有问题或建议，请提交 Issue 或联系维护者。
