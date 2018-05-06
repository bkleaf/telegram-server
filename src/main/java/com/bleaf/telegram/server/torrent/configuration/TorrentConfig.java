package com.bleaf.telegram.server.torrent.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class TorrentConfig {
    @Value("${telegram.btor.search.keyword.resolution}")
    String[] resolution;

    @Value("${telegram.btor.search.keyword.release}")
    String[] release;

    @Value("${telegram.btor.search.limit_size}")
    int limitSize;

    @Value("${telegram.btor.download.home}")
    String downloadHome;
}
