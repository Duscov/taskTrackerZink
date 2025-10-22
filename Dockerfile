############################
# 1) Build stage
############################
FROM eclipse-temurin:17-jdk AS builder
LABEL stage=builder

WORKDIR /workspace/app

# 1.1) Copy wrapper and scripts
COPY gradlew ./gradlew
COPY gradle ./gradle
RUN chmod +x ./gradlew

# 1.2) Copy project files and build jar
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

RUN ./gradlew --no-daemon clean bootJar -x test

RUN cp build/libs/*.jar app.jar

############################
# 2) Runtime stage
############################
FROM gcr.io/distroless/java17-debian11:nonroot AS runtime
LABEL stage=runtime

WORKDIR /app
COPY --from=builder /workspace/app/app.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]