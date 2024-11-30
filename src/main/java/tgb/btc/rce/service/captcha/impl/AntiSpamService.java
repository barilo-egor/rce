package tgb.btc.rce.service.captcha.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tgb.btc.library.service.process.VerifiedUserCache;
import tgb.btc.rce.service.captcha.IAntiSpamService;
import tgb.btc.rce.service.captcha.UserUpdateActivity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AntiSpamService implements IAntiSpamService {

    private final Map<Long, UserUpdateActivity> userActivityMap = new ConcurrentHashMap<>();

    private final int MESSAGE_LIMIT;

    private final static long TIME_WINDOW_MS = 10_000;

    private final VerifiedUserCache verifiedUserCache;

    public AntiSpamService(@Value("${allowed.count}") Integer allowedCount, VerifiedUserCache verifiedUserCache) {
        MESSAGE_LIMIT = allowedCount;
        this.verifiedUserCache = verifiedUserCache;
    }

    @Override
    public boolean isSpam(Long chatId) {
        if (verifiedUserCache.check(chatId)) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        UserUpdateActivity activity = userActivityMap.computeIfAbsent(chatId, id -> new UserUpdateActivity());
        activity.cleanOldMessages(currentTime, TIME_WINDOW_MS);
        if (activity.getMessageCount() >= MESSAGE_LIMIT) {
            return true;
        }
        activity.addMessage(currentTime);
        return false;
    }
}
