package tgb.btc.rce.config.serializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class LongRedisSerializer implements RedisSerializer<Long> {

    @Override
    public byte[] serialize(Long value) throws SerializationException {
        if (value == null) {
            return new byte[0];
        }
        return value.toString().getBytes();
    }

    @Override
    public Long deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        String str = new String(bytes);
        return Long.parseLong(str);
    }
}
