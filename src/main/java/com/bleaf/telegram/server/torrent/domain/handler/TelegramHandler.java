package com.bleaf.telegram.server.torrent.domain.handler;

import com.bleaf.telegram.server.torrent.service.TorrentService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void onUpdateReceived(Update update) {
        log.info("test receive = {}\n {}\n {}\n", update, update.getMessage().getChatId().toString(), update.getMessage().getText());

        SendMessage message = torrentService.getResponse(update);

        try {
            execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
