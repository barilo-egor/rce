package tgb.btc.lib.service.impl;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.lib.repository.UserRepository;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BannedUserCache {

    private static final Map<Long, Boolean> BANNED_USERS = new ConcurrentHashMap<>();

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void add(Long chatId, Boolean isBanned) {
        if (Objects.isNull(isBanned)) return;
        BANNED_USERS.put(chatId, isBanned);
    }

    public boolean get(Long chatId) {
        Boolean result = BANNED_USERS.get(chatId);
        if (Objects.nonNull(result)) return result;
        result = BooleanUtils.isTrue(userRepository.getIsBannedByChatId(chatId));
        BANNED_USERS.put(chatId, result);
        return result;
    }

    public void remove(Long chatId) {
        BANNED_USERS.remove(chatId);
    }
}
