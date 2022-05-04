package tgb.btc.rce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tgb.btc.rce.util.BotConfig;

@SpringBootApplication
public class RceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RceApplication.class, args);
        BotConfig.init();
    }

}
