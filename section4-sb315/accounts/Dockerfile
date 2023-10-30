FROM openjdk:17-jdk-slim

# remplaces deprecated MAINTAINER directive
LABEL org.opencontainers.image.authors="eazybytes.com"

COPY target/accounts-0.0.1-SNAPSHOT.jar accounts-0.0.1-SNAPSHOT.jar

# expose port
# EXPOSE 8080

# shellform
# ENTRYPOINT java,-jar,accounts-0.0.1-SNAPSHOT.jar

# shellform
ENTRYPOINT ["java","-jar","accounts-0.0.1-SNAPSHOT.jar"]