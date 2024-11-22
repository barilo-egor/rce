package tgb.btc.rce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tgb.btc.rce.config.serializer.UserStateRedisSerializer;
import tgb.btc.rce.enums.UserState;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, UserState> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, UserState> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new UserStateRedisSerializer());
        return template;
    }
}
