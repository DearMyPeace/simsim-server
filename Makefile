# 애플리케이션 이름과 도커 이미지 태그 설정
APP_NAME=simsim-server
IMAGE_NAME=simsim-server-image
DOCKER_FILE=Dockerfile
NETWORK_NAME=simsim-network

all: build up

build:
	./gradlew clean build

docker-build:
	docker compose build

up:
	docker compose up -d

clean: docker-clean
	./gradlew clean

fclean: clean
	docker network rm $(NETWORK_NAME) || true

docker-clean:
	docker compose down --remove-orphans