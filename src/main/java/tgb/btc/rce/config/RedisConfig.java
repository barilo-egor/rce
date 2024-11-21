package tgb.btc.rce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import tgb.btc.rce.config.serializer.LongRedisSerializer;
import tgb.btc.rce.config.serializer.UserStateRedisSerializer;
import tgb.btc.rce.enums.UserState;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<Long, UserState> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Long, UserState> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new LongRedisSerializer());
        template.setValueSerializer(new UserStateRedisSerializer());
        return template;
    }
}
