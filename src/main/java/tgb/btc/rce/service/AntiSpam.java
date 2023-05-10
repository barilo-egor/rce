package tgb.btc.rce.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AntiSpam {
    private static final Map<Long, Integer> MESSAGES_COUNTER = new ConcurrentHashMap<>();

    @Async
    public void saveTime(Long chatId) {
        synchronized (this) {
            Integer count = MESSAGES_COUNTER.get(chatId);
            if (Objects.isNull(count)) {
                MESSAGES_COUNTER.put(chatId, 1);
            } else {
                MESSAGES_COUNTER.put(chatId, count + 1);
            }
        }
    }

    @Scheduled(cron = "*/5 * * * * *")
    @Async
    public void check() {
        synchronized (this) {

        }
    }

}
