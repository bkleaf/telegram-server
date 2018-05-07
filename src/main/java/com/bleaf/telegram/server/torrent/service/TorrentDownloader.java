package com.bleaf.telegram.server.torrent.service;

import com.bleaf.telegram.server.torrent.model.SearchJob;

public interface TorrentDownloader {
    boolean download(String word, SearchJob searchJob);
}
