package com.bleaf.telegram.server.torrent.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "telegram.btor.download")
public class TorrentConfig {
    @Value("${telegram.btor.search.limit_size}")
    int limitSize;

//    @Value("${telegram.btor.download.home}")
    String downloadHome;

//    @Value("${telegram.btor.download.priority}")
    String[] priority;
}
