package com.bleaf.telegram.server.torrent.repository;

import com.bleaf.telegram.server.torrent.model.DownloadBox;
import com.bleaf.telegram.server.torrent.model.SearchCategory;

import java.util.Map;

public interface TorrentRopository {
    Map<String, DownloadBox> findByName(String name, SearchCategory searchCategory);
}
