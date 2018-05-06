package com.bleaf.telegram.server.torrent.service;

import com.bleaf.telegram.server.torrent.domain.tfreeca.repository.TfreecaRepository;
import com.bleaf.telegram.server.torrent.domain.tfreeca.service.DownloadService;
import com.bleaf.telegram.server.torrent.model.DownloadBox;
import com.bleaf.telegram.server.torrent.model.SearchJob;
import com.bleaf.telegram.server.torrent.model.SearchSite;
import com.bleaf.telegram.server.torrent.model.SearchStep;
import com.bleaf.telegram.server.torrent.repository.ResultJobStorage;
import com.bleaf.telegram.server.torrent.repository.TorrentRopository;
import com.bleaf.telegram.server.torrent.utility.MessageManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TorrentService {
    @Autowired
    TfreecaRepository tfreecaRepository;

    @Autowired
    ResultJobStorage resultJobStorage;

    @Autowired
    DownloadService downloadService;

    @Autowired
    MessageManager messageManager;

    public SendMessage getResponse(Update update) {
        String jobId = resultJobStorage.getJobId(
                update.getMessage().getFrom().getId(),
                update.getMessage().getChatId());

        SearchJob searchJob = resultJobStorage.getSearchJob(jobId);

        if(searchJob == null) {
            searchJob = new SearchJob();

            searchJob.setUserId(update.getMessage().getFrom().getId());
            searchJob.setChatId(update.getMessage().getChatId());

            resultJobStorage.storeSearchJob(jobId, searchJob);
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
            downloadService.download(update.getMessage().getText(), searchJob);
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


}
