#Imagen temporal de construccion del empaquetado

FROM eclipse-temurin:21-jdk-alpine as builder

WORKDIR /app

COPY pom.xml .
COPY .env .
COPY mvnw .
COPY .mvn .mvn

RUN ./mvnw dependency:go-offline
COPY src src
RUN ./mvnw clean package -DskipTests

#Imagen de ejecucion del empaquetado
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
EXPOSE 8091

COPY --from=builder /app/.env .
COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT [ "java", "-jar", "/app.jar" ]