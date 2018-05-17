#!/bin/bash

# transmission daemon start
service transmission-daemon start

java -Dspring.profiles.active=product \
    -Djava.security.egd=file:/dev/./urandom \
    -jar /data/src/telegram-server.jar \
    --spring.config.location=/data/config/config.yml > /data/logs/telegram-server.log