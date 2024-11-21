package tgb.btc.rce.service;

import tgb.btc.rce.enums.UserState;

public interface IRedisUserStateService {
    void save(Long chatId, UserState state);

    UserState get(Long key);

    void delete(Long key);
}
