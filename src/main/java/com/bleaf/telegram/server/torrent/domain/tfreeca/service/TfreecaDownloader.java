package com.bleaf.telegram.server.torrent.domain.tfreeca.service;

import com.bleaf.telegram.server.torrent.configuration.TorrentConfig;
import com.bleaf.telegram.server.torrent.domain.tfreeca.configuration.TfreecaConfig;
import com.bleaf.telegram.server.torrent.domain.tfreeca.repository.TfreecaRepository;
import com.bleaf.telegram.server.torrent.domain.tfreeca.utility.TfreecaUtils;
import com.bleaf.telegram.server.torrent.model.DownloadBox;
import com.bleaf.telegram.server.torrent.model.SearchJob;
import com.bleaf.telegram.server.torrent.service.TorrentDownloader;
import lombok.extern.slf4j.Slf4j;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TfreecaDownloader implements TorrentDownloader {
    @Autowired
    TfreecaConfig tfreecaConfig;

    @Autowired
    TorrentConfig torrentConfig;

    @Autowired
    TfreecaRepository tfreecaRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    TfreecaUtils tfreecaUtils;

    public boolean download(String word, SearchJob searchJob) {
        List<DownloadBox> downloadBoxes = tfreecaRepository.getDownloadList(word, searchJob);

        DownloadBox downloadBox = searchJob.getResultMap().get(word);
        String id = tfreecaUtils.getContentId(downloadBox.getUrl());

        try {
            for (DownloadBox box : downloadBoxes) {
                this.fileDownload(id, box);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean fileDownload(String id, DownloadBox downloadBox) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAll(this.getHeader(id));

        log.debug("download file url = {} : {}", downloadBox.getFileName(), downloadBox.getUrl());

        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                downloadBox.getUrl(), HttpMethod.GET, new HttpEntity(httpHeaders), byte[].class);

        byte[] body = responseEntity.getBody();

        String downloadFileName = torrentConfig.getDownloadHome() + File.separator + downloadBox.getFileName();
        log.debug("download file name = {}", downloadFileName);

        File file = new File(downloadFileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(body);
        fileOutputStream.close();

        return true;
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
}
