FROM openjdk:21-jdk-slim
WORKDIR /app
COPY . .
# 実行権限を付与
RUN chmod +x gradlew

# Gradleのビルド実行
RUN ./gradlew build --no-daemon
CMD ["java", "-jar", "build/libs/postApp-0.0.1-SNAPSHOT.jar"]


EXPOSE 8080  