FROM ubuntu:latest As build
RUN apt-get update && \
    apt-get install -y git maven openjdk-11-jdk
RUN git clone https://github.com/Pritam-Khergade/student-ui.git
WORKDIR /student-ui
RUN mvn clean package
FROM tomcat:9
COPY --from=build /student-ui/target/*.war /webapps/student.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
