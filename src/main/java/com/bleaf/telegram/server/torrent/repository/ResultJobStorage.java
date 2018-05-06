package com.bleaf.telegram.server.torrent.repository;

import com.bleaf.telegram.server.torrent.model.SearchJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class ResultJobStorage {
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
}
