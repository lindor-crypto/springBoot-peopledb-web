# Étape 1 : Build (facultatif si tu build déjà via Jenkins)
# FROM gradle:8.4.0-jdk17 AS build
# COPY --chown=gradle:gradle . /home/gradle/project
# WORKDIR /home/gradle/project
# RUN gradle build -x test

# Étape 2 : Image finale
FROM eclipse-temurin:17-jdk-alpine

# Dossier de travail dans le conteneur
WORKDIR /app

# Copie du .jar généré
COPY build/libs/*.jar app.jar

# Port exposé (si utilisé dans ECS ou EC2)
EXPOSE 8080

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]
