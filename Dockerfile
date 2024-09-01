FROM openjdk:17-alpine
LABEL authors="eykin"
WORKDIR opt/transaction
COPY target/transaction-0.0.1-SNAPSHOT.jar transaction.jar
ENTRYPOINT ["java", "-jar", "transaction.jar"]