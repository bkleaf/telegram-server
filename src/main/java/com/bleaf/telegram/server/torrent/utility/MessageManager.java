package com.bleaf.telegram.server.torrent.utility;

import com.bleaf.telegram.server.torrent.model.SearchCategory;
import com.bleaf.telegram.server.torrent.model.SearchJob;
import com.bleaf.telegram.server.torrent.model.SearchSite;
import com.bleaf.telegram.server.torrent.model.SearchStep;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MessageManager {

    public boolean parser(String msg, SearchJob searchJob) {

        log.debug("parsing message = {}", msg);

        if (Strings.isNullOrEmpty(msg)) {
            log.error("msg is null");
        }

        if(searchJob.getCurrentSearchStep() == null) {
            return this.setSearchJob(msg, searchJob);
        } else {
            return this.setDownloadJob(msg, searchJob);
        }
    }

    private boolean setSearchJob(String msg, SearchJob searchJob) {
        List<String> texts = Splitter.on(" ")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(msg);

        SearchCategory searchCategory = this.getCategory(texts.get(0));

        if (searchCategory == null) {
            return false;
        }

        String searchWord = Joiner.on(" ")
                .skipNulls()
                .join(texts.subList(1, texts.size()));

        searchJob.setSearchSite(SearchSite.ALL);
        searchJob.setSearchCategory(searchCategory);
        searchJob.setCurrentSearchStep(SearchStep.SEARCH);
        searchJob.setComplate(false);
        searchJob.setWord(searchWord);

        return true;
    }

    private boolean setDownloadJob(String msg, SearchJob searchJob) {
        if (searchJob.getResultMap() == null
                || searchJob.getResultMap().isEmpty()) {

            log.error("검색 결과가 없어, 다운 로드 할 수 없습니다");
            return false;
        }

        if (!searchJob.getResultMap().containsKey(msg)) {
            log.error("선택한 다운로드 메시지가 잘못되었습니다. = {}", msg);
            return false;
        }

        searchJob.setCurrentSearchStep(SearchStep.DOWNLOAD);
        searchJob.setSearchSite(searchJob.getResultMap().get(msg).getSearchSite());
        searchJob.setComplate(false);

        return true;
    }

    public SendMessage makeSendMessage(SearchJob searchJob) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(searchJob.getChatId());

        String msg = "";
        if (!searchJob.isComplate()) {
            if(searchJob.getCurrentSearchStep() == null) {
                msg += "잘못된 메시지 입니다.\n다시 시도해 주세요";
            } else {

                msg += "작업 : " + searchJob.getCurrentSearchStep().name() + "\n";

                if (searchJob.getCurrentSearchStep() == SearchStep.SEARCH) {
                    msg += "검색어 : " + searchJob.getWord() + "\n";
                } else {
                    msg += "선택 파일 명 : " + searchJob.getWord() + "\n";
                }

                msg += "작업에 실패 하였습니다. \n다시 시도해 주세요.";
            }
        } else {
            if (searchJob.getCurrentSearchStep() == SearchStep.SEARCH) {
                sendMessage.setReplyMarkup(
                        this.makeKeyboard(searchJob.getResultList()));

                msg += "작업이 완료 되었습니다. \n";
                msg += "아래의 List중 하나를 선택하면 다운로드를 시작합니다.";
            } else {
                msg += "Torrent file download가 완료 되었습니다.";
            }
        }

        sendMessage.setText(msg);

        return sendMessage;
    }

    public ReplyKeyboardMarkup makeKeyboard(List<String> resultList) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row;

        for (String result : resultList) {
            row = new KeyboardRow();
            row.add(result);

            keyboard.add(row);
        }

        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

    private SearchCategory getCategory(String word) {
        log.debug("first word = {}", word);

        if (!word.startsWith("/")) {
            return null;
        }

        return SearchCategory
                .getSearchCategory(
                        word.replaceFirst("/", ""));
    }
}
