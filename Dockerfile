FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew clean build --no-daemon

FROM eclipse-temurin:21-jdk AS runtime
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]