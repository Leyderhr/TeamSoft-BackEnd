# ==========================================
# Etapa de construcción (JDK 21)
# ==========================================
FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /workspace/app

# Copiamos el wrapper de Maven y sus carpetas ocultas
COPY mvnw .
COPY .mvn .mvn

# Damos permisos de ejecución al wrapper (para evitar errores en Linux/Docker)
RUN chmod +x ./mvnw

# Copiamos el archivo de configuración de dependencias
COPY pom.xml .

# Copiamos la carpeta lib (vital si tienes librerías locales que el pom.xml necesita)
COPY lib lib

# Copiamos el código fuente
COPY src src

# Compilamos el proyecto creando el ejecutable final
RUN ./mvnw clean package -DskipTests

# ==========================================
# Etapa de ejecución (JRE 21)
# ==========================================
FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp

# Copiamos SOLO el .jar generado al contenedor final para que pese menos
COPY --from=build /workspace/app/target/*.jar app.jar

# Exponemos el puerto 10000 para Render
EXPOSE 10000 

# Arrancamos la aplicación
ENTRYPOINT ["java","-jar","/app.jar"]
