package tgb.btc.rce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tgb.btc.rce.exception.InitPropertyValueNotFoundException;
import tgb.btc.rce.service.impl.UpdateDispatcher;
import tgb.btc.rce.util.BotConfig;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableWebMvc
public class RceApplication extends WebMvcAutoConfiguration {

    public static void main(String[] args) throws InitPropertyValueNotFoundException {
        UpdateDispatcher.applicationContext = SpringApplication.run(RceApplication.class, args);
        BotConfig.init();
    }

}
