package tgb.btc.rce.service.impl.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.RedisPrefix;
import tgb.btc.rce.service.redis.IRedisStringService;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@Service
public class RedisStringService implements IRedisStringService {

    private final String prefix;

    private final RedisTemplate<String, String> redisTemplate;

    public RedisStringService(RedisTemplate<String, String> redisTemplate, @Value("${bot.name}") String botName) {
        this.redisTemplate = redisTemplate;
        this.prefix = botName + ":" + "string_";
    }

    @Override
    public void save(Long chatId, String value) {
        redisTemplate.opsForValue().set(prefix + chatId, value, Duration.of(20, ChronoUnit.MINUTES));
    }

    @Override
    public void save(RedisPrefix prefix, Long chatId, String value) {
        redisTemplate.opsForValue().set(prefix.getPrefix() + chatId, value, Duration.of(20, ChronoUnit.MINUTES));
    }

    @Override
    public String get(Long key) {
        return redisTemplate.opsForValue().get(prefix + key);
    }

    @Override
    public String get(RedisPrefix prefix, Long key) {
        return redisTemplate.opsForValue().get(prefix.getPrefix() + key);
    }

    @Override
    public void delete(Long key) {
        redisTemplate.delete(prefix + key);
    }

    @Override
    public void delete(RedisPrefix prefix, Long key) {
        redisTemplate.delete(prefix.getPrefix() + key);
    }

    @Override
    public void deleteAll(Long key) {
        redisTemplate.delete(prefix + key.toString());
        Arrays.stream(RedisPrefix.values()).forEach(redisPrefix -> delete(redisPrefix, key));
    }
}
