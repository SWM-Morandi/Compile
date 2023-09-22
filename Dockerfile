FROM openjdk:11-jre-slim-buster

RUN apt-get update && apt-get install -y python3 g++-11 openjdk-11-jdk

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
