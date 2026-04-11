# Stage 1 - BUILD
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

# Stage 2 - RUN
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]