FROM azul/zulu-openjdk:latest
MAINTAINER Max De Marzi<maxdemarzi@gmail.com>
EXPOSE 8080
COPY $ROOT/target/server-1.0-SNAPSHOT.jar server-1.0-SNAPSHOT.jar
CMD ["java", "-jar", "server-1.0-SNAPSHOT.jar"]
