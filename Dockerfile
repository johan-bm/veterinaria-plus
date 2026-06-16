# =========================================================
# Dockerfile multi-stage para Veterinaria+
#
# Etapa 1 (build): compila el proyecto con Maven dentro de un
# contenedor temporal que incluye el JDK completo.
#
# Etapa 2 (runtime): copia únicamente el .jar ya compilado a una
# imagen liviana con solo el JRE, reduciendo el tamaño final de la
# imagen y la superficie de ataque (no quedan herramientas de build
# ni código fuente en la imagen de producción).
# =========================================================

# ---- Etapa 1: build ----
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar primero solo el pom.xml para aprovechar el cache de capas
# de Docker: si las dependencias no cambian, esta capa se reutiliza
# y no es necesario volver a descargarlas en cada build.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y construir el .jar (sin correr pruebas
# aquí, ya que las pruebas se ejecutan en una etapa separada del
# pipeline de CI/CD antes de llegar a este paso de construcción)
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Etapa 2: runtime ----
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# wget es necesario para el healthcheck de docker-compose
RUN apt-get update && apt-get install -y --no-install-recommends wget \
    && rm -rf /var/lib/apt/lists/*

# Usuario no root por buenas prácticas de seguridad en contenedores
RUN groupadd -r veterinaria && useradd -r -g veterinaria veterinaria

COPY --from=build /app/target/veterinaria-plus.jar app.jar

RUN chown veterinaria:veterinaria app.jar
USER veterinaria

EXPOSE 8080

# Perfil "docker" por defecto: se conecta a la base de datos PostgreSQL
# por el nombre de servicio "db" definido en docker-compose.yml.
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]
