package tgb.btc.rce.service.redis;

import tgb.btc.rce.enums.RedisPrefix;

public interface IRedisStringService {
    void save(Long chatId, String value);

    void save(RedisPrefix prefix, Long chatId, String value);

    String get(Long key);

    String get(RedisPrefix prefix, Long key);

    void delete(Long key);

    void delete(RedisPrefix prefix, Long key);

    void deleteAll(Long key);

    void nextStep(Long key);

    void previousStep(Long key);

    void saveStep(Long key, Integer value);

    Integer getStep(Long key);
}
