FROM openjdk:8-jdk-alpine
#ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8789
RUN mkdir /imgUploads
VOLUME /imgUploads
ADD target/onlinestore-0.0.1-SNAPSHOT.jar store.jar
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n","-jar","store.jar"]