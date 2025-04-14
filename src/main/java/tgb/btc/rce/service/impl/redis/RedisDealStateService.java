package tgb.btc.rce.service.impl.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.DealState;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.service.redis.IRedisDealStateService;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class RedisDealStateService implements IRedisDealStateService {

    private final String prefix;

    private final RedisTemplate<String, DealState> redisTemplate;

    public RedisDealStateService(RedisTemplate<String, DealState> redisTemplate, @Value("${bot.name}") String botName) {
        this.redisTemplate = redisTemplate;
        this.prefix = botName + ":" + "dealState_";
    }

    @Override
    public void save(Long chatId, DealState state) {
        redisTemplate.opsForValue().set(prefix + chatId, state, Duration.of(20, ChronoUnit.MINUTES));
    }

    @Override
    public DealState get(Long key) {
        return redisTemplate.opsForValue().get(prefix + key);
    }

    @Override
    public void delete(Long key) {
        redisTemplate.delete(prefix + key);
    }
}
