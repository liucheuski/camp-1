#Start with a base image containing Java runtime
FROM openjdk:17 as build

# Add Maintainer Info
LABEL maintainer="Siarhei Liucheuski <liucheuski@gmail.com>"

# The application's jar file
ARG JAR_FILE=target/*.jar

# Add the application's jar to the container
COPY ${JAR_FILE} app.jar

EXPOSE 8090

#execute the application
ENTRYPOINT ["java","-jar","/app.jar"]