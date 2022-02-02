SRC				=			WebServer

run: build
	java $(SRC)

build:
	javac $(SRC).java

clean:
	rm -rf conf/
	find . -name "*.class" -type f -delete
	find . -name "*.jar" -type f -delete

build-dev-docker:
	docker build . -t web-server-finalsolution-dev:latest

dev-docker: build-dev-docker
	docker run \
	--rm \
	-it \
	-v $(shell pwd):/root/web-server-finalsolution \
	-p 8080:8080 \
	web-server-finalsolution-dev:latest

doc:
	pandoc documentation.md -o documentation.pdf

.PHONY: run build clean build-dev-docker dev-docker doc
