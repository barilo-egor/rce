package tgb.btc.rce.config.serializer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tgb.btc.rce.enums.UserState;

import static org.junit.jupiter.api.Assertions.*;

class UserStateRedisSerializerTest {

    private final UserStateRedisSerializer userStateRedisSerializer = new UserStateRedisSerializer();

    @Test
    @DisplayName("Должен вернуть пустой массив байтов.")
    void serializeShouldReturnEmptyByteArray() {
        assertArrayEquals(new byte[0], userStateRedisSerializer.serialize(null));
    }

    @Test
    @DisplayName("Должен вернуть массив байтов из UserState.")
    void serialize() {
        for (UserState userState : UserState.values()) {
            assertArrayEquals(userState.name().getBytes(), userStateRedisSerializer.serialize(userState));
        }
    }

    @Test
    @DisplayName("Должен вернуть UserState из массива байтов")
    void deserialize() {
        for (UserState userState : UserState.values()) {
            byte[] bytes = userState.name().getBytes();
            assertEquals(userState, userStateRedisSerializer.deserialize(bytes));
        }
    }

    @Test
    @DisplayName("Должен вернуть null, если передан null.")
    void deserializeShouldReturnNullIfNull() {
        assertNull(userStateRedisSerializer.deserialize(null));
    }

    @Test
    @DisplayName("Должен вернуть null если передан пустой массив байтов.")
    void deserializeShouldReturnNullIfEmpty() {
        assertNull(userStateRedisSerializer.deserialize(new byte[0]));
    }
}