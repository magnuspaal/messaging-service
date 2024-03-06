FROM maven:3.8.1-openjdk-17-slim AS builder

WORKDIR /app

COPY src ./src
COPY pom.xml .
RUN mvn -f /app/pom.xml clean
RUN mvn -f /app/pom.xml install
RUN mvn -f /app/pom.xml package

FROM openjdk:17-alpine
ARG APP_VERSION

COPY --from=builder /app/target/messaging-service-${APP_VERSION}.jar /usr/local/lib/messaging-service.jar

EXPOSE 8080

ENTRYPOINT java -jar /usr/local/lib/messaging-service.jar