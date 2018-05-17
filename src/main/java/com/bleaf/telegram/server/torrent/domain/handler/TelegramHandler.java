package com.bleaf.telegram.server.torrent.domain.handler;

import com.bleaf.telegram.server.torrent.service.TorrentService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

@Data
@Slf4j
@Component
public class TelegramHandler extends TelegramLongPollingBot {
    @Autowired
    TorrentService torrentService;

    @Value("${telegram.btor.name}")
    String name;

    @Value("${telegram.btor.token}")
    String token;

    // chat id : 266254817
    // token : 361649822:AAGlPqdO3Af8J1hbsuETWx7pdHv1-6FY3Xk
    // chat bot : btor_bot
    @Override
    public void onUpdateReceived(Update update) {
        log.debug("test receive = {}\n {}\n {}\n", update, update.getMessage().getChatId().toString(), update.getMessage().getText());
        log.info("telegram message = {} : {} : {}",
                update.getMessage().getChatId(),
                update.getMessage().getFrom().getFirstName(),
                update.getMessage().getText());

        SendMessage message = torrentService.getResponse(update);

        if(message != null) {
            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
