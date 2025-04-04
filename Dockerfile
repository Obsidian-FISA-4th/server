# 1. OpenJDK 17 기반 이미지 사용
FROM eclipse-temurin:17-jdk as build

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 현재 디렉토리의 모든 파일을 컨테이너로 복사
COPY . .

# 4. Gradle 캐시 최적화 (이미지 빌드 속도 향상)
RUN chmod +x gradlew
RUN ./gradlew clean build -x test

# 5. 최종 실행 이미지
FROM eclipse-temurin:17-jdk as runtime
WORKDIR /app

# 6. 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 7. 컨테이너 실행 시 JAR 파일 실행
CMD ["java", "-jar", "app.jar"]

