FROM azul/zulu-openjdk:21

WORKDIR /app

# 复制Maven文件
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# 复制源代码
COPY src src

# 构建应用
RUN ./mvnw clean package -DskipTests

# 运行应用
EXPOSE 8080

CMD ["java", "-jar", "target/queue-router-1.0.0.jar"]
