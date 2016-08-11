FROM centos
MAINTAINER adriano@argl.eng.br

ENV PORT 8081
EXPOSE 8081
EXPOSE 22
EXPOSE 7000 7001 7199 9042 9160

RUN yum -y install wget \
    && rm -fr /usr/local/twittargl \
    && wget -O /tmp/twittargl.tar.gz 'http://www.argl.eng.br/twittargl/supportfiles/twittargl.tar.gz' \
    && cd /tmp/ \
    && tar -zxf twittargl.tar.gz \
    && mv /tmp/twittargl /usr/local/ \
    && rm -f /tmp/twittargl.tar.gz \
    && yum -y install epel-release \
    && yum -y install initscripts \
    && cp /usr/local/twittargl/files/datastax.repo /etc/yum.repos.d/ \
    && yum -y clean all && yum -y update \
    && yum -y localinstall /usr/local/twittargl/files/jdk-8u102-linux-x64.rpm \
    && cp /usr/local/twittargl/files/java.sh /etc/profile.d/ \
    && source /etc/profile.d/java.sh \
    && yum -y install python-pip python-wheel \
    && pip install tweepy \
    && pip install cassandra-driver \
    && wget -O /tmp/scala-2.10.1.tgz 'http://www.scala-lang.org/files/archive/scala-2.10.1.tgz' \
    && cd /tmp/ \
    && tar -zxf scala-2.10.1.tgz \
    && mv /tmp/scala-2.10.1 /usr/lib \
    && ln -s /usr/lib/scala-2.10.1 /usr/lib/scala \
    && wget -O /tmp/spark-1.6.2-bin-hadoop2.6.tgz 'http://d3kbcqa49mib13.cloudfront.net/spark-1.6.2-bin-hadoop2.6.tgz' \
    && cd /tmp/ \
    && tar -zxf spark-1.6.2-bin-hadoop2.6.tgz \
    && mkdir /usr/local/spark \
    && cp -r /tmp/spark-1.6.2-bin-hadoop2.6/* /usr/local/spark \
    && rm -rf /tmp/spark-1.6.2-bin-hadoop2.6 \
    && yum -y install cassandra22 cassandra22-tools \
    && yum -y install `cat /usr/local/twittargl/files/nodejs_files` \
    && npm install express \
    && npm install cassandra-driver \
    && npm install async \
    && ln -s /usr/local/twittargl/bin/twittargl-run /usr/local/bin

VOLUME /run /tmp

CMD ["systemctl", "enable", "cassandra.service"]

CMD ["cqlsh", "-f", "/usr/local/twittargl/files/twitterdb.cql", "$HOSTNAME"]

CMD ["/usr/sbin/init"]

