package com.bleaf.telegram.server.torrent.domain.tfreeca.repository;

import com.bleaf.telegram.server.torrent.configuration.TorrentConfig;
import com.bleaf.telegram.server.torrent.domain.tfreeca.configuration.TfreecaConfig;
import com.bleaf.telegram.server.torrent.domain.tfreeca.model.TfreecaMode;
import com.bleaf.telegram.server.torrent.domain.tfreeca.model.UserAgent;
import com.bleaf.telegram.server.torrent.model.DownloadBox;
import com.bleaf.telegram.server.torrent.model.SearchCategory;
import com.bleaf.telegram.server.torrent.model.SearchSite;
import com.bleaf.telegram.server.torrent.repository.TorrentRopository;
import com.google.common.base.Strings;
import com.google.common.net.UrlEscapers;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class TfreecaRepository implements TorrentRopository {
    @Autowired
    TfreecaConfig tfreecaConfig;

    @Autowired
    TorrentConfig torrentConfig;

    @Override
    public Map<String, DownloadBox> findByName(String name, SearchCategory searchCategory) {
        Map<String, DownloadBox> list = null;

        String searchURL = null;
        try {
            boolean loop = true;
            int page = 1;
            while(loop) {
                searchURL = this.getSearchURL(name, searchCategory, TfreecaMode.list, page++);

                Document document = Jsoup.connect(searchURL)
                        .userAgent(UserAgent.getUserAgent())
                        .header("Accept-Encoding", "gzip, deflate")
                        .get();

                Elements rows = document.select("table.b_list tbody tr td.subject div.list_subject a[class~=stitle[0-9]*]");

                if(rows == null || rows.size() == 0) {
                    loop = false;
                    continue;
                }

                if(list == null) {
                    list = new HashMap();
                }

                String title, href;
                for (Element row : rows) {
                    title = row.text();
                    href = row.attr("href");

                    if (Strings.isNullOrEmpty(title) || Strings.isNullOrEmpty(href)) {
                        log.error("search result is null = {}", row.toString());
                        continue;
                    }

                    if(isValid(title)) {
                        list.put(title, new DownloadBox(SearchSite.Tfreeca, href));
                    }

                    if(list.size() >= torrentConfig.getLimitSize()) {
                        loop = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("error html parsing = {} : {}", searchURL, e.getMessage());
        }

        return list;
    }

    private String getSearchURL(String name, SearchCategory searchCategory, TfreecaMode tfreecaMode, int page) {

        if (Strings.isNullOrEmpty(name) || searchCategory == null || tfreecaMode == null) {
            log.error("name or category or mode is null = {} : {} : {}", name, searchCategory, tfreecaMode);

            return null;
        }

        String paramsURL = tfreecaConfig.getSearchUrl();
        paramsURL += "?b_id=" + searchCategory.getId(SearchSite.Tfreeca);
        paramsURL += "&mode=" + tfreecaMode.name();
        paramsURL += "&sc=" + UrlEscapers.urlFormParameterEscaper().escape(name);
        paramsURL += "&x=0&y=0";
        paramsURL += "&page=" + page;

        log.debug("Tfreeca search uri = {}", paramsURL);

        return paramsURL;
    }

    private boolean isValid(String title) {
        boolean result1 = false;
        boolean result2 = false;

        if(torrentConfig.getResolution() != null && torrentConfig.getResolution().length > 0) {
            for(String resolution : torrentConfig.getResolution()) {
                result1 = (title.indexOf(resolution) != -1);

                if(result1) {
                    break;
                }
            }
        } else {
            result1 = true;
        }

        if(torrentConfig.getRelease() != null && torrentConfig.getRelease().length > 0) {
            for(String release : torrentConfig.getRelease()) {
                result2 = (title.indexOf(release) != -1);

                if(result2) {
                    break;
                }
            }
        } else {
            result2 = true;
        }

        return  (result1 & result2);
    }
}
