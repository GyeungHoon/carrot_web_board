# Build stage
FROM amazoncorretto:21.0.4 AS build
WORKDIR /app

# 현재 디렉토리의 모든 파일을 컨테이너로 복사
COPY . .

# gradlew에 실행 권한을 부여 (만약 권한이 없다면)
RUN chmod +x ./gradlew

# gradlew로 빌드 (테스트 제외)
RUN ./gradlew build -x test

# Runtime stage
FROM amazoncorretto:21.0.4 AS runtime
WORKDIR /app

# 빌드된 JAR 파일을 복사
COPY --from=build /app/build/libs/*.jar /app/server.jar

EXPOSE 8080
CMD ["java", "-jar", "/app/server.jar"]
