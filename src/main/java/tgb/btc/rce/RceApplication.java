package tgb.btc.rce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import tgb.btc.rce.service.impl.UpdateDispatcher;
import tgb.btc.rce.util.BotConfig;
import tgb.btc.rce.util.BotPropertiesUtil;

@SpringBootApplication
@EnableAsync
public class RceApplication {

    public static void main(String[] args) {
        BotPropertiesUtil.loadProperties();
        UpdateDispatcher.applicationContext = SpringApplication.run(RceApplication.class, args);
        BotConfig.init();
    }

}
