SRC								=						WebServer

DEV_DOCKERFILE		=						Dockerfile.dev
DEV_DOCKER_NAME		=						web-server-finalsolution-dev:latest

DOC_IN						=						documentation.md
DOC_OUT						=						documentation.pdf

run: build
	java $(SRC)

build:
	javac $(SRC).java

clean:
	find . -name "*.class" -type f -delete
	find . -name "*.jar" -type f -delete

build-dev-docker:
	docker build -f $(DEV_DOCKERFILE) . -t $(DEV_DOCKER_NAME)

dev-docker: build-dev-docker
	docker run \
	--rm \
	-it \
	-v $(shell pwd):/root/web-server-finalsolution \
	-p 8080:8080 \
	$(DEV_DOCKER_NAME)

doc:
	pandoc $(DOC_IN) -o $(DOC_OUT)

doc-docker:
	docker run --rm \
	--volume "$(shell pwd):/data" \
	--user $(shell id -u):$(shell id -g) \
	pandoc/latex $(DOC_IN) -o $(DOC_OUT)

.PHONY: run build clean build-dev-docker dev-docker doc doc-docker
