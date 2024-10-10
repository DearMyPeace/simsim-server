# Amazon Corretto JDK 21 이미지 사용
FROM amazoncorretto:21-alpine3.18

# 작업 디렉토리 설정
WORKDIR .

# 로컬에서 빌드된 JAR 파일을 Docker 컨테이너로 복사
COPY build/libs/*.jar spring.jar

# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "spring.jar"]

# 애플리케이션이 사용하는 포트 노출
EXPOSE 8081