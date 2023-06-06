package tgb.btc.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tgb.btc.lib.exception.InitPropertyValueNotFoundException;
import tgb.btc.lib.service.impl.UpdateDispatcher;
import tgb.btc.lib.util.BotConfig;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@Slf4j
public class RceApplication {

    public static void main(String[] args) throws InitPropertyValueNotFoundException {
        UpdateDispatcher.applicationContext = SpringApplication.run(RceApplication.class, args);
        BotConfig.init();
    }

}
