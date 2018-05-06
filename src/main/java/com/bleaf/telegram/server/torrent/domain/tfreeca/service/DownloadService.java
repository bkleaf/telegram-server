package com.bleaf.telegram.server.torrent.domain.tfreeca.service;

import com.bleaf.telegram.server.torrent.domain.tfreeca.configuration.TfreecaConfig;
import com.bleaf.telegram.server.torrent.domain.tfreeca.model.UserAgent;
import com.bleaf.telegram.server.torrent.model.DownloadBox;
import com.bleaf.telegram.server.torrent.model.SearchJob;
import com.bleaf.telegram.server.torrent.model.SearchSite;
import com.google.common.base.Splitter;
import com.sun.research.ws.wadl.HTTPMethods;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DownloadService {
    @Autowired
    TfreecaConfig tfreecaConfig;

    @Autowired
    RestTemplate restTemplate;

    public void download(String word, SearchJob searchJob) {
        DownloadBox downloadBox = searchJob.getResultMap().get(word);

        String detailUrl = downloadBox.getUrl();

        String b_id = searchJob.getSearchCategory().getId(SearchSite.Tfreeca);
        String id = this.getContentId(detailUrl);

        String downloadUrl = tfreecaConfig.getDownloadUrl()
                + "?link=" + Base64.getEncoder().encodeToString((b_id + "|" + id + "|").getBytes());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAll(this.getHeader(id));

        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                downloadUrl, HttpMethod.GET, new HttpEntity(httpHeaders), byte[].class);

        byte[] body = responseEntity.getBody();

        File lOutFile = new File("/vmap/test.torrent");
        FileOutputStream lFileOutputStream = null;
        try {
            lFileOutputStream = new FileOutputStream(lOutFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            lFileOutputStream.write(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            lFileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> getHeader(String id) {
        Map<String, String> header = new HashMap();

        header.put("Connection", "keep-alive");
        header.put("Content-Type", "application/x-bittorrent");
        header.put("pragma", "no-cache");
        header.put("expires", "0");
        header.put("Content-Disposition", "attachment; filename=\"" + id + ".torrent\"");
        header.put("content-description", "php generated data");

        return header;
    }

    private String getContentId(String url) {
        log.debug("detail url = {}", url);

        List<String> words = Splitter.on("&").trimResults().splitToList(url);

        for (String word : words) {
            if (word.startsWith("id=")) {
                return word.replace("id=", "");
            }
        }

        return null;
    }
}
