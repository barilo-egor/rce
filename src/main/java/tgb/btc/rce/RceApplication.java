package tgb.btc.rce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tgb.btc.rce.service.impl.UpdateDispatcher;
import tgb.btc.rce.util.AntiSpamPropertiesUtil;
import tgb.btc.rce.util.BotConfig;
import tgb.btc.rce.util.BotPropertiesUtil;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class RceApplication {

    public static void main(String[] args) {
        BotPropertiesUtil.loadProperties();
        AntiSpamPropertiesUtil.loadProperties();
        BotConfig.init(); // TODO загружать проперти по первому обращению(PropertiesReader)
        UpdateDispatcher.applicationContext = SpringApplication.run(RceApplication.class, args);
        BotConfig.init();
    }

}
