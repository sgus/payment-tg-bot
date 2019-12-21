package my.app;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class TgBotApp {

    private static final Logger log = Logger.getLogger(TgBotApp.class);

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(TgBotApp.class, args);

        log.debug("started");
    }
}
