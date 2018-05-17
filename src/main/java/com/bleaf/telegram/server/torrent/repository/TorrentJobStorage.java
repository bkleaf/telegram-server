package com.bleaf.telegram.server.torrent.repository;

import com.bleaf.telegram.server.torrent.model.SearchJob;
import com.bleaf.telegram.server.torrent.model.SearchStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class TorrentJobStorage {
    Map<String, SearchJob> storage = new HashMap();

    public void storeSearchJob(String jobId, SearchJob searchJob) {
        this.storage.put(jobId, searchJob);
    }

    public SearchJob getSearchJob(String jobId) {
        return this.storage.get(jobId);
    }

    public String getJobId(int userId, long chatId) {
        return (userId + "_" + chatId);
    }

    public void remove(String jobId) {
        storage.remove(jobId);
    }

    public SearchJob getDownloadChectId(String fileName) {
        for(SearchJob searchJob : storage.values()) {
            if(searchJob.getCurrentSearchStep() == SearchStep.DOWNLOAD
                    && searchJob.getDownloadFileName().equalsIgnoreCase(fileName)) {

                return searchJob;
            }
        }

        return null;
    }
}
