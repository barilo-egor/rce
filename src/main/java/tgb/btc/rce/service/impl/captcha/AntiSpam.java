package tgb.btc.rce.service.impl.captcha;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.service.process.VerifiedUserCache;
import tgb.btc.rce.service.captcha.IAntiSpam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class AntiSpam implements IAntiSpam {

    private static final List<Long> SPAM_USERS = new ArrayList<>();

    private static final Map<Long, Integer> MESSAGES_COUNTER = new ConcurrentHashMap<>();

    private final Map<Long, String> captchaCash = new ConcurrentHashMap<>();

    private VerifiedUserCache verifiedUserCache;

    @Autowired
    public void setVerifiedUserCache(VerifiedUserCache verifiedUserCache) {
        this.verifiedUserCache = verifiedUserCache;
    }

    @Override
    public void putToCaptchaCash(Long chatId, String captcha) {
        captchaCash.put(chatId, captcha);
    }

    @Override
    public String getFromCaptchaCash(Long chatId) {
        return captchaCash.get(chatId);
    }

    @Override
    public void removeFromCaptchaCash(Long chatId) {
        captchaCash.remove(chatId);
    }

    public boolean isVerifiedUser(Long chatId) {
        return false;
//        return verifiedUserCache.check(chatId);
    }

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
            log.debug("Пользователь chatId={} добавлен в спам-контроль.", chatId);
        }
    }

    public void removeUser(Long chatId) {
        synchronized (SPAM_USERS) {
            SPAM_USERS.removeIf(userChatId -> userChatId.equals(chatId));
        }
    }
}
