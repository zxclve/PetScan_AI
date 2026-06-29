FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# 미리 빌드된 JAR 파일 복사 (로컬에서 빌드)
COPY build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
