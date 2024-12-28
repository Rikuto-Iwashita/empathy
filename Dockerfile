FROM openjdk:21-jdk-slim
WORKDIR /app
COPY . .
RUN ./gradlew build || ./gradlew.bat build
CMD ["java", "-jar", "build/libs/postApp-0.0.1-SNAPSHOT.jar"]


EXPOSE 8080  