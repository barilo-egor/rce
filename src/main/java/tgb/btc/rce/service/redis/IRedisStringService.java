package tgb.btc.rce.service.redis;

public interface IRedisStringService {
    void save(Long chatId, String value);

    String get(Long key);

    void delete(Long key);
}
