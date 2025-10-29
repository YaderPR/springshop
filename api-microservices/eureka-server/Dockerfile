#Imagen temporal de construccion del empaquetado

FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
# RUN ./mvnw dependency:go-offline
RUN ./mvnw dependency:resolve
COPY src src
RUN ./mvnw clean package -DskipTests

#Imagen de ejecucion del empaquetado
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
EXPOSE 8761

COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT [ "java", "-jar", "/app.jar" ]