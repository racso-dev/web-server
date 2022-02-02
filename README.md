# web-server-finalsolution

## Team members
+ Oscar RENIER
+ Nicolas ANTOINE

## How to run

### For grading

1. `rm -rf conf`
2. `find . -name "*.class" -type f -delete`
3. `find . -name "*.jar" -type f -delete`
4. `cp myConf ./conf`
5. `javac WebServer.java`
6. `java WebServer`

### In development

1. `make`

### With docker

Clone the repo, then run `make docker-dev`, you will be spawned inside a shell
where you can run `make` to test out the program.
All changes to the code will be synced since the repo is mounted inside the
