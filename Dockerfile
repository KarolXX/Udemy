FROM openjdk:11

ARG KEYCLOAK_REALM
ARG KEYCLOAK_AUTH_SERVER_URL
ARG KEYCLOAK_SECRET
ARG KEYCLOAK_CLIENT_ID

ENV KEYCLOAK_REALM=$KEYCLOAK_REALM
ENV KEYCLOAK_AUTH_SERVER_URL=$KEYCLOAK_AUTH_SERVER_URL
ENV KEYCLOAK_SECRET=$KEYCLOAK_SECRET
ENV KEYCLOAK_CLIENT_ID=$KEYCLOAK_CLIENT_ID

COPY target/udemy-0.0.1-SNAPSHOT.jar udemy-app-0.0.1-SNAPSHOT.jar
EXPOSE 8080

ENTRYPOINT ["java","-jar","/udemy-app-0.0.1-SNAPSHOT.jar"]