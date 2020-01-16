FROM maven:3.6.0-jdk-11-slim AS build

COPY pom.xml /pom.xml
COPY src /src/

RUN mvn clean package -f /pom.xml

# Run stage
FROM openjdk:12

ENV BOT_TOKEN=UNSET

RUN mkdir /data/
COPY --from=build /target/Hawk-jar-with-dependencies.jar /Hawk.jar

CMD /usr/bin/java -jar /Hawk.jar $BOT_TOKEN
