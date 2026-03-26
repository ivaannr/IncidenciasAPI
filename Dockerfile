FROM eclipse-temurin:24-jdk AS build

RUN apt-get update && apt-get install -y maven
WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:24-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
