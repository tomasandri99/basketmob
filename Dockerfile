# ---- Build stage ----
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# cache deps
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

# build
COPY src ./src
RUN mvn -DskipTests clean package

# ---- Run stage ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# copy the fat jar built above (use your exact artifact name)
COPY --from=build /app/target/basketmob-0.0.1-SNAPSHOT.jar app.jar

# Render routes traffic to the port your app binds to.
# We'll bind to $PORT (Render sets it; default 10000).
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
