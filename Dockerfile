# 1. 베이스 이미지 (Java 21)
FROM amazoncorretto:21 AS builder
WORKDIR /app

# 1) 라이브러리 설치에 필요한 파일만 먼저 복사 (소스코드 제외)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 2) 권한 주고, 라이브러리만 미리 다운로드 (dependencies)
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# 3) 이제서야 소스코드 복사
COPY src src

# 4) 빌드 (아까 받은 라이브러리 그대로 씀)
RUN ./gradlew build --no-daemon -x test

# 2. 빌드된 Jar 파일을 컨테이너 안으로 가져옴
# (libs 폴더 밑에 jar가 딱 하나만 있어야 함)
# build.gradle에서 이름을 고정시켰으니 그냥 카피만 한줄 써줌
# ARG JAR_FILE=build/libs/*.jar
# COPY ${JAR_FILE} app.jar
FROM amazoncorretto:21
WORKDIR /app
COPY --from=builder /app/build/libs/bookstore-api.jar app.jar

# 3. 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
