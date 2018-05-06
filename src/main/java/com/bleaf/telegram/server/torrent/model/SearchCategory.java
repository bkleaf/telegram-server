package com.bleaf.telegram.server.torrent.model;

import lombok.Getter;

public enum SearchCategory {
    MOVIE(new String[]{"tmovie"}),
    DRAMA(new String[]{"tdrama"}),
    ENTER(new String[]{"tent"}),
    MUSIC(new String[]{"tmusic"}),
    TV(new String[]{"tv"}),
    ANI(new String[]{"tani"});

    private String[] ids;

    SearchCategory(String[] ids) {
        this.ids = ids;
    }

    public String getId(SearchSite searchSite) {
        return this.ids[searchSite.getNumber()];
    }

    public static SearchCategory getSearchCategory(String keyword) {
        switch (keyword) {
            case "e" : return MOVIE;
            case "a" : return DRAMA;
            case "r" : return ENTER;
            case "c" : return MUSIC;
            case "v" : return TV;
            case "i" : return ANI;
            default: return null;
        }
    }

}
