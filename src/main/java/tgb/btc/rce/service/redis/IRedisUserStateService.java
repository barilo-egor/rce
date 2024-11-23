package tgb.btc.rce.service.redis;

import tgb.btc.rce.enums.UserState;

public interface IRedisUserStateService {
    void save(Long chatId, UserState state);

    UserState get(Long key);

    void delete(Long key);
}
