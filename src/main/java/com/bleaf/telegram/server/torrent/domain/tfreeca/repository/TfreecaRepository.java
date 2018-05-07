package com.bleaf.telegram.server.torrent.domain.tfreeca.repository;

import com.bleaf.telegram.server.torrent.configuration.TorrentConfig;
import com.bleaf.telegram.server.torrent.domain.tfreeca.configuration.TfreecaConfig;
import com.bleaf.telegram.server.torrent.domain.tfreeca.utility.TfreecaUtils;
import com.bleaf.telegram.server.torrent.model.*;
import com.bleaf.telegram.server.torrent.repository.TorrentRopository;
import com.bleaf.telegram.server.torrent.repository.filter.SearchFilter;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import jersey.repackaged.com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class TfreecaRepository implements TorrentRopository {
    @Autowired
    TfreecaConfig tfreecaConfig;

    @Autowired
    TorrentConfig torrentConfig;

    @Autowired
    SearchFilter searchFilter;

    @Autowired
    TfreecaUtils tfreecaUtils;

    @Override
    public Map<String, DownloadBox> findByName(String name, SearchCategory searchCategory) {
        Map<String, DownloadBox> list = null;

        String searchURL = null;
        try {
            boolean loop = true;
            int page = 1;
            while (loop) {
                searchURL = tfreecaUtils.getSearchURL(name, searchCategory, page++);

                Document document = Jsoup.connect(searchURL)
                        .userAgent(UserAgent.getUserAgent())
                        .header("Accept-Encoding", "gzip, deflate")
                        .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                        .header("Connection", "keep-alive")
                        .header("Host", tfreecaConfig.getHost())
                        .get();

                Elements rows = document.select("table.b_list tbody tr td.subject div.list_subject a[class~=(stitle)*[0-9]*]");

                if (rows == null || rows.size() == 0) {
                    loop = false;
                    continue;
                }

                if (list == null) {
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

                    if (searchFilter.isValid(title)) {
                        list.put(title, new DownloadBox(SearchSite.Tfreeca, "", href));
                    }

                    if (list.size() >= torrentConfig.getLimitSize()) {
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

    public List<DownloadBox> getDownloadList(String title, SearchJob searchJob) {
        log.info("downlad title = {}", title);

        DownloadBox downloadBox = searchJob.getResultMap().get(title);
        String detailUrl = tfreecaConfig.getBaseUrl() + "/" + downloadBox.getUrl();

        Elements rows = null;
        try {
            Document document = Jsoup.connect(detailUrl)
                    .userAgent(UserAgent.getUserAgent())
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                    .header("Connection", "keep-alive")
                    .header("Host", tfreecaConfig.getHost())
                    .referrer(searchJob.getSearchCategory().getId(SearchSite.Tfreeca))
                    .get();

            rows = document.select("table tbody tr td table tbody tr td.view_t4 a.font11");
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Element> elements = new ArrayList();
        String fileName;
        for (Element row : rows) {
            fileName = row.text();
            if (Strings.isNullOrEmpty(fileName)) {
                continue;
            }

            elements.add(row);
        }

        Element subtitle = this.getSubtitleFileName(elements);

        List<String> conditions = Lists.newArrayList(torrentConfig.getConditions());
        conditions.add(0, Files.getNameWithoutExtension(subtitle.text()));

        Element torrent = this.getTorrentRow(elements, conditions, 0);

        List<DownloadBox> downloadBoxes = new ArrayList();

        String b_id = searchJob.getSearchCategory().getId(SearchSite.Tfreeca);
        String id = tfreecaUtils.getContentId(detailUrl);
        for (int i = 0; i < elements.size(); i++) {
            if (torrent.text().equals(elements.get(i).text())
                    || subtitle.text().equals(elements.get(i).text())) {

                downloadBoxes.add(new DownloadBox(SearchSite.Tfreeca, elements.get(i).text(), tfreecaUtils.getDownloadUrl(b_id, id, i)));
            }
        }

        return downloadBoxes;
    }

    public Element getTorrentRow(List<Element> rows, List<String> conditions, int idx) {
        for (Element row : rows) {
            if(!row.text().toLowerCase().endsWith("torrent")) {
                continue;
            }

            if (row.text().indexOf(conditions.get(idx)) != -1) {
                return row;
            }
        }

        return getTorrentRow(rows, conditions, ++idx);
    }

    /*
    1080p -> 720p
     */
    public Element getSubtitleFileName(List<Element> rows) {
        List<Element> subtitles = new ArrayList();

        String fileName = null;
        for (Element row : rows) {
            fileName = row.text();

            if (fileName.endsWith("smi") || fileName.endsWith("srt")) {
                subtitles.add(row);
            }
        }

        Element result = null;
        for (Element subtitle : subtitles) {
            result = subtitle;

            if (subtitle.text().toLowerCase().indexOf("1080p") != -1
                    || subtitle.text().toLowerCase().indexOf("720p") != -1) {
                break;
            }
        }

        log.debug("subtitle file name = {}", result.text());

        return result;
    }
}
