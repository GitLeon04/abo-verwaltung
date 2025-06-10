FROM eclipse-temurin:17-jre

WORKDIR /app

# greift automatisch die gerade gebaute JAR – unabhängig von der Versionsnummer
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
