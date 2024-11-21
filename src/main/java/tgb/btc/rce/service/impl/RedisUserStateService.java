package tgb.btc.rce.service.impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.service.IRedisUserStateService;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class RedisUserStateService implements IRedisUserStateService {

    private final RedisTemplate<Long, UserState> redisTemplate;

    public RedisUserStateService(RedisTemplate<Long, UserState> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(Long chatId, UserState state) {
        redisTemplate.opsForValue().set(chatId, state, Duration.of(20, ChronoUnit.MINUTES));
    }

    @Override
    public UserState get(Long key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(Long key) {
        redisTemplate.delete(key);
    }
}
