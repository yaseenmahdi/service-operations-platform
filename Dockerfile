FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 10001 serviceops
COPY --from=build /workspace/target/service-operations-platform-1.0.0.jar app.jar
USER serviceops
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
