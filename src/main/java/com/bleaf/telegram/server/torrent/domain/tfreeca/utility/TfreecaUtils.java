package com.bleaf.telegram.server.torrent.domain.tfreeca.utility;

import com.bleaf.telegram.server.torrent.domain.tfreeca.configuration.TfreecaConfig;
import com.bleaf.telegram.server.torrent.model.*;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.net.UrlEscapers;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TfreecaUtils {
    @Autowired
    TfreecaConfig tfreecaConfig;

    public String getSearchURL(String name, SearchCategory searchCategory, int page) {

        if (Strings.isNullOrEmpty(name) || searchCategory == null) {
            log.error("name or category or mode is null = {} : {}", name, searchCategory);

            return null;
        }

        String paramsURL = tfreecaConfig.getSearchUrl();
        paramsURL += "?b_id=" + searchCategory.getId(SearchSite.Tfreeca);
        paramsURL += "&mode=list";
        paramsURL += "&sc=" + UrlEscapers.urlFormParameterEscaper().escape(name);
        paramsURL += "&x=0&y=0";
        paramsURL += "&page=" + page;

        log.debug("Tfreeca search uri = {}", paramsURL);

        return paramsURL;
    }

    public String getContentId(String url) {
        log.debug("detail url = {}", url);

        List<String> words = Splitter.on("&").trimResults().splitToList(url);

        for (String word : words) {
            if (word.startsWith("id=")) {
                return word.replace("id=", "");
            }
        }

        return null;
    }

    public String getDownloadUrl(String b_id, String id, int idx) {
        return tfreecaConfig.getDownloadUrl()
                + "?link=" + Base64.getEncoder().encodeToString((b_id + "|" + id + "|" + idx).getBytes());
    }
}
