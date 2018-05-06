package com.bleaf.telegram.server.torrent.model;

import lombok.Getter;

public enum SearchSite {
    Tfreeca(0), ALL(1);

    @Getter
    int number;

    SearchSite(int number) {
        this.number  = number;
    }
}