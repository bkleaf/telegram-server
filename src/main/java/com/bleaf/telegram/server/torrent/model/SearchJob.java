package com.bleaf.telegram.server.torrent.model;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SearchJob {
    int userId;
    long chatId;
    String word;

    SearchSite searchSite;
    SearchCategory searchCategory;

    SearchStep currentSearchStep;

    Map<String, DownloadBox> resultMap;

    String downloadFileName;

    boolean isComplate;

    public List<String> getResultList() {
        return Lists.newArrayList(resultMap.keySet());
    }
}
