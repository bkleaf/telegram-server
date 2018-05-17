package com.bleaf.telegram.server.torrent.model;

public enum SearchKeyword {
    m,              // movie
    d,             // drama
    e,              // entertainment
    s,              // music
    t,               // tv
    a;              // ani

    public static boolean contains(String keyword) {
        for(SearchKeyword searchKeyword : SearchKeyword.values()) {
            if(searchKeyword.name().equals(keyword.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
