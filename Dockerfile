FROM ubuntu:14.04

ENV DEBIAN_FRONTEND noninteractive

RUN apt-get -y update

RUN apt-get install -y language-pack-ko

# set locale ko_KR
RUN locale-gen ko_KR.UTF-8

ENV LANG ko_KR.UTF-8
ENV LANGUAGE ko_KR.UTF-8
ENV LC_ALL ko_KR.UTF-8

#set time seoul
RUN mv /etc/localtime /etc/localimte_origin
RUN ln -s /usr/share/zoneinfo/Asia/Seoul /etc/localtime

RUN apt-get purge openjdk*

RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y software-properties-common && \
    add-apt-repository ppa:webupd8team/java -y && \
    apt-get update && \
    echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections && \
    apt-get install -y oracle-java8-installer && \
    apt-get install -y transmission-daemon && \
    apt-get install -y curl && \
    apt-get clean

ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

RUN service transmission-daemon stop

RUN mv /etc/transmission-daemon/settings.json /etc/transmission-daemon/settings.json.old

RUN mkdir -p /data/src /data/downloads /data/torrents /data/config /data/logs

RUN chown -R debian-transmission:debian-transmission /data

COPY ./docker/settings.json /etc/transmission-daemon/settings.json
COPY ./docker/run.sh /data/src/
COPY ./docker/torrent_push.sh /data/src/
COPY ./target/telegram-server-0.0.1-SNAPSHOT.jar /data/src/telegram-server.jar

RUN chmod +x /data/src/run.sh /data/src/torrent_push.sh

VOLUME ["/data/downloads", "/data/torrents", "/data/config", "/data/logs"]

ENTRYPOINT ["sh","/data/src/run.sh"]

EXPOSE 9091 7021
