# Connect Modification to add the JDBC connector
# https://github.com/confluentinc/cp-docker-images/blob/3.3.x/debian/kafka-connect/Dockerfile
FROM confluentinc/cp-kafka-connect:4.0.0


MAINTAINER github@gerardnico.com

# A classpath aware location where to put the Jars
# The content of the first location /etc/kafka-connect/jars/ on the classpath seems to be suppressed
# We put the jar then with the other kafka jar
ENV CLASSPATH_DIR=/usr/share/java/kafka/
ENV TEMP_DIR=/tmp

# The RUN instruction will execute any commands in a new layer on top of the current image and commit the results. 
# https://docs.docker.com/engine/reference/builder/#run
RUN echo "===> Installing the Jdbc jars ...\n" \
    && cp /usr/share/java/kafka-connect-jdbc/sqlite-jdbc-3.8.11.2.jar ${CLASSPATH_DIR} \
    && cp /usr/share/java/kafka-connect-jdbc/postgresql-9.4-1206-jdbc41.jar ${CLASSPATH_DIR} \
    && echo "===> Jars listing after installation in ${CLASSPATH_DIR}\n" \
    && ls ${CLASSPATH_DIR} \
    && echo "===> Update apt-get ...\n" \
    && apt-get -qq update \
    && echo "===> Installing Jq\n"  \
    && apt-get install -qq jq \
    && echo "===> Installing sqlite\n"  \
    && apt-get install -qq sqlite \
    && echo "===> Cleaning up ...\n"  \
    && apt-get -qq autoremove \
    && apt-get -qq clean && rm -rf ${TEMP_DIR}/*

# In distributed mode, you need to use the CONNECT API 
# and the connector configuration file must be in Json
COPY source-quickstart-sqlite.json /etc/kafka-connect-jdbc/



