FROM adoptopenjdk:16_36-jre-hotspot as  jar_extraction
ARG JAR_FILE
WORKDIR extracted
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM adoptopenjdk:16_36-jre-hotspot
WORKDIR application
COPY --from=jar_extraction extracted/dependencies/ ./
COPY --from=jar_extraction extracted/spring-boot-loader/ ./
COPY --from=jar_extraction extracted/snapshot-dependencies/ ./
COPY --from=jar_extraction extracted/application/ ./
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]