# ======== 1단계: Build Stage ========
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

COPY . .

# 캐시를 활용한 build 속도 최적화
RUN gradle clean build -x test

# ======== 2단계: Run Stage ========
FROM eclipse-temurin:21-jre as runtime

WORKDIR /app

# 빌드 결과 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
