package tgb.btc.rce.config.serializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import tgb.btc.rce.enums.DealState;
import tgb.btc.rce.enums.UserState;

public class DealStateRedisSerializer implements RedisSerializer<DealState> {

    @Override
    public byte[] serialize(DealState value) throws SerializationException {
        if (value == null) {
            return new byte[0];
        }
        return value.name().getBytes();
    }

    @Override
    public DealState deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        String name = new String(bytes);
        return Enum.valueOf(DealState.class, name);
    }
}
