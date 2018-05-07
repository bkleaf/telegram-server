package com.bleaf.telegram.server.torrent.service;

import com.bleaf.telegram.server.torrent.domain.tfreeca.repository.TfreecaRepository;
import com.bleaf.telegram.server.torrent.domain.tfreeca.service.TfreecaDownloader;
import com.bleaf.telegram.server.torrent.model.DownloadBox;
import com.bleaf.telegram.server.torrent.model.SearchJob;
import com.bleaf.telegram.server.torrent.model.SearchSite;
import com.bleaf.telegram.server.torrent.model.SearchStep;
import com.bleaf.telegram.server.torrent.repository.TorrentJobStorage;
import com.bleaf.telegram.server.torrent.repository.TorrentRopository;
import com.bleaf.telegram.server.torrent.utility.MessageManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TorrentService {
    @Autowired
    TfreecaRepository tfreecaRepository;

    @Autowired
    TorrentJobStorage torrentJobStorage;

    @Autowired
    TfreecaDownloader tfreecaDownloader;

    @Autowired
    MessageManager messageManager;

    public SendMessage getResponse(Update update) {
        String jobId = torrentJobStorage.getJobId(
                update.getMessage().getFrom().getId(),
                update.getMessage().getChatId());

        SearchJob searchJob = torrentJobStorage.getSearchJob(jobId);

        if(searchJob == null) {
            searchJob = new SearchJob();

            searchJob.setUserId(update.getMessage().getFrom().getId());
            searchJob.setChatId(update.getMessage().getChatId());

            torrentJobStorage.storeSearchJob(jobId, searchJob);
        }

        boolean jobResult = messageManager.parser(
                update.getMessage().getText(), searchJob);

        if(!jobResult) {
            return messageManager.makeSendMessage(searchJob);
        }

        if (searchJob.getCurrentSearchStep() == SearchStep.SEARCH) {
            TorrentRopository[] torrentRopositories = this.getTorrentRepository(
                    searchJob.getSearchSite());

            Map<String, DownloadBox> resultList = new HashMap();
            for (TorrentRopository torrentRopository : torrentRopositories) {


                Map<String, DownloadBox> result = torrentRopository
                        .findByName(searchJob.getWord(),
                                searchJob.getSearchCategory());

                resultList.putAll(result);
            }

            if(resultList != null && !resultList.isEmpty()) {
                searchJob.setResultMap(resultList);
                searchJob.setComplate(true);
            }
        } else if(searchJob.getCurrentSearchStep() == SearchStep.DOWNLOAD) {
            TorrentDownloader torrentDownloader = this.getTorrentDownloader(searchJob.getSearchSite());

            boolean result = false;
            if(torrentDownloader != null) {
                result = torrentDownloader.download(update.getMessage().getText(), searchJob);
            }

            searchJob.setComplate(result);
            torrentJobStorage.remove(jobId);
        }


        return messageManager.makeSendMessage(searchJob);
    }

    private TorrentRopository[] getTorrentRepository(SearchSite searchSite) {
        if (searchSite == SearchSite.ALL) {
            return new TorrentRopository[]{
                    tfreecaRepository
            };
        } else if (searchSite == SearchSite.Tfreeca) {
            return new TorrentRopository[]{tfreecaRepository};
        }

        return null;
    }

    private TorrentDownloader getTorrentDownloader(SearchSite searchSite) {
        if(searchSite == SearchSite.Tfreeca) {
            return tfreecaDownloader;
        }

        return null;
    }
}
