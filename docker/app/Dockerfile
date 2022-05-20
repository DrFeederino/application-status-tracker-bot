FROM azul/zulu-openjdk-alpine:17.0.3-17.34.19-jre

RUN addgroup -S user-app && adduser -S user-app -G user-app
USER user-app:user-app

ARG JAR_FILE=application-tracking.jar
COPY ${JAR_FILE} /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]