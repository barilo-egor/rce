package tgb.btc.rce.service;

import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.rce.conditional.AntispamCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Conditional(AntispamCondition.class)
public class AntiSpam {

    private static final List<Long> SPAM_USERS = new ArrayList<>();

    private static final Map<Long, Integer> MESSAGES_COUNTER = new ConcurrentHashMap<>();

    public static final Map<Long, String> CAPTCHA_CASH = new ConcurrentHashMap<>();

    public boolean isSpamUser(Long chatId) {
        synchronized (SPAM_USERS) {
            for (Long userChatId : SPAM_USERS) {
                if (userChatId.equals(chatId)) return true;
            }
            return false;
        }
    }

    @Async
    public void saveTime(Long chatId) {
        synchronized (this) {
            Integer messagesCount = MESSAGES_COUNTER.get(chatId);
            int newMessageCount = 1;
            if (Objects.nonNull(messagesCount)) newMessageCount = messagesCount + 1;
            MESSAGES_COUNTER.put(chatId, newMessageCount);
        }
    }

    @Scheduled(cron = "*/5 * * * * *")
    @Async
    public void check() {
        synchronized (this) {
            int allowedCount = PropertiesPath.ANTI_SPAM_PROPERTIES.getInteger("allowed.count", 20);
            for (Map.Entry<Long, Integer> entry : MESSAGES_COUNTER.entrySet()) {
                if (entry.getValue() > allowedCount) addUser(entry.getKey());
            }
            MESSAGES_COUNTER.clear();
        }
    }

    public void addUser(Long chatId) {
        synchronized (SPAM_USERS) {
            SPAM_USERS.add(chatId);
        }
    }

    public void removeUser(Long chatId) {
        synchronized (SPAM_USERS) {
            SPAM_USERS.removeIf(userChatId -> userChatId.equals(chatId));
        }
    }
}
