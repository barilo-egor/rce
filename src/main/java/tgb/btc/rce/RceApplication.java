package tgb.btc.rce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication(scanBasePackages = {"tgb.btc", "org.telegram"})
@EnableScheduling
@EnableAsync
@EnableCaching
@EnableWebMvc
@EnableJpaRepositories("tgb.btc")
@EntityScan("tgb.btc")
public class RceApplication extends WebMvcAutoConfiguration {

    public static ApplicationContext SPRING_CONTEXT;

    public static void main(String[] args) {
        SPRING_CONTEXT = SpringApplication.run(RceApplication.class, args);
    }

}
