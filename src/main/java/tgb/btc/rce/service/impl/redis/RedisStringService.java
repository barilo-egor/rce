package tgb.btc.rce.service.impl.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tgb.btc.rce.service.redis.IRedisStringService;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class RedisStringService implements IRedisStringService {

    private final String prefix = "string_";

    private final RedisTemplate<String, String> redisTemplate;

    public RedisStringService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(Long chatId, String value) {
        redisTemplate.opsForValue().set(prefix + chatId, value, Duration.of(20, ChronoUnit.MINUTES));
    }

    @Override
    public String get(Long key) {
        return redisTemplate.opsForValue().get(prefix + key);
    }

    @Override
    public void delete(Long key) {
        redisTemplate.delete(prefix + key);
    }
}
