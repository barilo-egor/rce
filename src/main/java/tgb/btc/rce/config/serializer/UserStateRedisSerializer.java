package tgb.btc.rce.config.serializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import tgb.btc.rce.enums.UserState;

public class UserStateRedisSerializer implements RedisSerializer<UserState> {

    @Override
    public byte[] serialize(UserState value) throws SerializationException {
        if (value == null) {
            return new byte[0];
        }
        return value.name().getBytes();
    }

    @Override
    public UserState deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        String name = new String(bytes);
        return Enum.valueOf(UserState.class, name);
    }
}
