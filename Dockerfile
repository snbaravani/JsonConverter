FROM maven:3-openjdk-11

WORKDIR /usr/local/runme

# Copy all source code in and compile the app into a .jar file
COPY . .
RUN mvn package

# Create the target dir for input data to be copied into at container runtime
RUN mkdir -p /avarni/reports

ENTRYPOINT ["java", "-jar", "target/json-transformer.jar"]
#ENTRYPOINT ["ls", "/avarni/reports"]
