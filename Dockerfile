FROM openjdk:8-jre-alpine
COPY service/build/libs/service.jar service.jar
EXPOSE 8080
CMD java ${JAVA_OPTS} -jar service.jar