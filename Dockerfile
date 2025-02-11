FROM openjdk:17-jdk

ENV WILDFLY_HOME /opt/wildfly
ENV WILDFLY_VERSION 30.0.0.Final

# Создаем директорию для WildFly
RUN mkdir -p $WILDFLY_HOME

# Копируем WildFly из вашей локальной директории в контейнер
COPY wildfly-30.0.0.Final/ $WILDFLY_HOME

EXPOSE 8080 9990

# Устанавливаем рабочую директорию
WORKDIR $WILDFLY_HOME

# Запускаем WildFly в standalone режиме
CMD ["bin/standalone.sh", "-b", "0.0.0.0"]