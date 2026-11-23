FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /src
COPY pom.xml ./
COPY discovery-service ./discovery-service
COPY config-service ./config-service
COPY customer-service ./customer-service
COPY ebank-service ./ebank-service
COPY gateway-service ./gateway-service
COPY chatbot-service ./chatbot-service
ARG MODULE
RUN mvn -B -pl ${MODULE} -am -DskipTests package
FROM eclipse-temurin:21-jre
ARG MODULE
WORKDIR /app
COPY --from=builder /src/${MODULE}/target/${MODULE}-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
