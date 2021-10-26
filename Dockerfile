FROM openjdk:11.0.5-slim
ARG JAR_FILE=target/json-transformer.jar
ARG JAR_LIB_FILE=target/lib/

WORKDIR /usr/local/runme

COPY ${JAR_FILE} app.jar

ADD ${JAR_LIB_FILE} lib/
#mounting host machine's dir on to the docler's, where reports will be available for processing
ADD . /avarni/reports

# java -jar /usr/local/runme/app.jar
ENTRYPOINT ["java","-jar","app.jar"]