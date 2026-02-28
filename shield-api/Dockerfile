FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B
COPY . .
RUN ./mvnw clean package -DskipTests

EXPOSE 8080
ENTRYPOINT ["java","-jar","target/shield-api-0.0.1-SNAPSHOT.jar"]