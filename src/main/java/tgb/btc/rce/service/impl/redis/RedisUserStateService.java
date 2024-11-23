package tgb.btc.rce.service.impl.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.service.redis.IRedisUserStateService;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class RedisUserStateService implements IRedisUserStateService {

    private final String prefix = "state_";

    private final RedisTemplate<String, UserState> redisTemplate;

    public RedisUserStateService(RedisTemplate<String, UserState> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(Long chatId, UserState state) {
        redisTemplate.opsForValue().set(prefix + chatId, state, Duration.of(20, ChronoUnit.MINUTES));
    }

    @Override
    public UserState get(Long key) {
        return redisTemplate.opsForValue().get(prefix + key);
    }

    @Override
    public void delete(Long key) {
        redisTemplate.delete(prefix + key);
    }
}
