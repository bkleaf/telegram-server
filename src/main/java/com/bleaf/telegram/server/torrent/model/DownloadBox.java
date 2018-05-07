package com.bleaf.telegram.server.torrent.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DownloadBox {
    SearchSite searchSite;
    String fileName;
    String url;
}
