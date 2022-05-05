package tgb.btc.rce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tgb.btc.rce.service.impl.UpdateDispatcher;
import tgb.btc.rce.util.BotConfig;
import tgb.btc.rce.util.BotPropertiesUtil;

@SpringBootApplication
public class RceApplication {

    public static void main(String[] args) {
        BotPropertiesUtil.loadProperties();
        UpdateDispatcher.applicationContext = SpringApplication.run(RceApplication.class, args);
        BotConfig.init();
    }

}
