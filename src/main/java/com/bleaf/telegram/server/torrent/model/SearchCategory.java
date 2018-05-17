package com.bleaf.telegram.server.torrent.model;

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
            case "m":
                return MOVIE;
            case "d":
                return DRAMA;
            case "e":
                return ENTER;
            case "s":
                return MUSIC;
            case "t":
                return TV;
            case "a":
                return ANI;
            default:
                return null;
        }
    }

}
