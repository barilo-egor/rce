package tgb.btc.rce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tgb.btc.rce.service.impl.UpdateDispatcher;
import tgb.btc.rce.util.BotConfig;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class RceApplication {

    public static void main(String[] args) {
        UpdateDispatcher.applicationContext = SpringApplication.run(RceApplication.class, args);
        BotConfig.init();
    }

}
