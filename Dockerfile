FROM eclipse-temurin:11.0.17_8-jre-ubi9-minimal
COPY ./dist-cache-app/build/libs/dist-cache-app.jar /usr/
EXPOSE 8085
CMD ["java", "-jar", "/usr/dist-cache-app.jar"]