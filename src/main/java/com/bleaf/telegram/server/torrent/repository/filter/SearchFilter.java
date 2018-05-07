package com.bleaf.telegram.server.torrent.repository.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "telegram.btor.search.filter")
public class SearchFilter {
    String[] keywords;

    public boolean isValid(String subject) {
        boolean[] valid = new boolean[keywords.length];

        String[] words;
        String keyword;
        for(int i=0; i < keywords.length; i++) {
            keyword = keywords[i];
            words = keyword.split(",");

            for(String word : words) {
                valid[i] = (subject.indexOf(word) != -1);

                if(valid[i]) {
                    break;
                }
            }

            if(!valid[i]) {
                break;
            }
        }

        boolean result = true;
        for(int i=0; i < valid.length; i++) {
            result = result & valid[i];
        }

        return result;
    }
}
