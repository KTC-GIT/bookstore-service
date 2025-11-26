# 1. 베이스 이미지 (Java 21)
FROM amazoncorretto:21

# 2. 빌드된 Jar 파일을 컨테이너 안으로 가져옴
# (libs 폴더 밑에 jar가 딱 하나만 있어야 함)
# build.gradle에서 이름을 고정시켰으니 그냥 카피만 한줄 써줌
# ARG JAR_FILE=build/libs/*.jar
# COPY ${JAR_FILE} app.jar
COPY build/libs/bookstore-api.jar app.jar

# 3. 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
