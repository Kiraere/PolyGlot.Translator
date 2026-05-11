# Етап збірки
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# Явно кажемо Докеру зайти в папку з Java-кодом
WORKDIR /app/backend
RUN mvn clean package -DskipTests

# Етап запуску
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Забираємо зібраний .jar файл з правильної папки
COPY --from=build /app/backend/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]