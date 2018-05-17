package com.bleaf.telegram.server.torrent.presentation;

import com.bleaf.telegram.server.torrent.service.TorrentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/torrent")
public class TorrentController {
    @Autowired
    TorrentService torrentService;

    @RequestMapping("/complate")
    public void downloadComplate(@RequestParam("msg") String msg) {
        log.info("torrent download complate = {}", msg);

        torrentService.downloadComplate(msg);
    }
}
