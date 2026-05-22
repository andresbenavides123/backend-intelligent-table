# Etapa 1: Construcción
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY . .
# Construimos el proyecto ignorando los tests para un despliegue más rápido
RUN mvn clean package -DskipTests

# Etapa 2: Imagen de producción
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiamos el JAR generado desde la etapa de construcción
COPY --from=builder /app/smart-api-infrastructure/target/*.jar app.jar

# Exponemos el puerto de la aplicación
EXPOSE 8080

# Variable de entorno por defecto para el puerto, puede ser sobreescrita por plataformas como Railway/Render
ENV PORT=8080

# Comando para iniciar la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
