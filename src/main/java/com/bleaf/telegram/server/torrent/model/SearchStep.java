package com.bleaf.telegram.server.torrent.model;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import java.util.List;

public enum SearchStep {
    HELP, SEARCH, DOWNLOAD, NONE;

    public static SearchStep getStep(String msg) {
        if(Strings.isNullOrEmpty(msg)) {
            return NONE;
        }

        List<String> texts = Splitter.on(" ")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(msg);

        String keyword = texts.get(0).toLowerCase();

        if(keyword.equals("/h") || keyword.equals("/help")) {
            return SearchStep.HELP;
        } else if(keyword.startsWith("/")) {
            return SearchStep.SEARCH;
        }

        return SearchStep.DOWNLOAD;
    }
}
