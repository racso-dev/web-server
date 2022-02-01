SRC				=			WebServer

run: build
	java $(SRC)

build:
	javac $(SRC).java

clean:
	rm -rf conf/
	find . -name "*.class" -type f -delete
	find . -name "*.jar" -type f -delete

.PHONY: run build clean