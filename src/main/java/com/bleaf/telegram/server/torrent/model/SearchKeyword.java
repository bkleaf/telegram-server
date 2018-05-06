package com.bleaf.telegram.server.torrent.model;

public enum SearchKeyword {
    e,              // move
    a,              // drama
    r,              // entertainment
    c,              // music
    v,              // tv
    i;              // ani

    public static boolean contains(String keyword) {
        for(SearchKeyword searchKeyword : SearchKeyword.values()) {
            if(searchKeyword.name().equals(keyword.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
