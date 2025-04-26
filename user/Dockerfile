# 使用官方的 OpenJDK 基础镜像
FROM openjdk:8-jdk-slim

# 设置工作目录
WORKDIR /app

# 将构建好的 Spring Boot JAR 文件复制到镜像中
COPY target/user-0.0.1-SNAPSHOT.jar /app/user-0.0.1-SNAPSHOT.jar

# 暴露应用程序的端口（默认为 8080）
EXPOSE 3230

# 启动 Spring Boot 应用程序
CMD ["java", "-jar", "user-0.0.1-SNAPSHOT.jar"]