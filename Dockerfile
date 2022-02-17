FROM gradle:7.1-jdk16 AS build
COPY --chown=gradle:gradle . /hawk
WORKDIR /hawk
RUN gradle shadowJar --no-daemon

FROM openjdk:11.0.8-jre-slim
RUN mkdir /data/
COPY --from=build /hawk/build/libs/Hawk.jar /
RUN dir

ENTRYPOINT ["java", "-jar", "/Hawk.jar"]
