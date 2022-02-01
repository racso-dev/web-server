SRC					=				WebServer

run: build
	java $(SRC)

build:
	javac $(SRC).java

.PHONY: all