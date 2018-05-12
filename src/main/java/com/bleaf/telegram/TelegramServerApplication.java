package com.bleaf.telegram;

import com.bleaf.telegram.server.torrent.domain.tfreeca.repository.TfreecaRepository;
import com.bleaf.telegram.server.torrent.model.SearchCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Base64;

@Slf4j
@SpringBootApplication
public class TelegramServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelegramServerApplication.class, args);
    }
}
