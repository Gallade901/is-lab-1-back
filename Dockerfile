FROM openjdk:17-jdk

ENV WILDFLY_HOME /opt/wildfly
ENV WILDFLY_VERSION 30.0.0.Final

RUN mkdir -p $WILDFLY_HOME

COPY wildfly-30.0.0.Final/ $WILDFLY_HOME

EXPOSE 8080 9990

WORKDIR $WILDFLY_HOME

CMD ["bin/standalone.sh", "-b", "0.0.0.0"]