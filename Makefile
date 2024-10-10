# 애플리케이션 이름과 도커 이미지 태그 설정
APP_NAME=simsim-server
IMAGE_NAME=simsim-server-image
DOCKER_FILE=Dockerfile

# 모든 빌드 및 도커 이미지를 빌드하는 명령어
all: build docker-build docker-run

# 기본 Gradle 빌드 및 클린 명령어
build:
	./gradlew clean build

clean: docker-clean
	./gradlew clean

# 도커 빌드 명령어
docker-build:
	docker build -t $(IMAGE_NAME) -f $(DOCKER_FILE) .

# 도커 실행 명령어
docker-run:
	docker run -d -p 8081:8081 $(IMAGE_NAME)

# 도커 컨테이너 정리 (실행 중인 컨테이너를 종료하고 삭제)
docker-clean:
	docker stop $(shell docker ps -q --filter ancestor=$(IMAGE_NAME)) || true
	docker rm $(shell docker ps -a -q --filter ancestor=$(IMAGE_NAME)) || true
