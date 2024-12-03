package tgb.btc.rce.service.captcha.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.btc.library.service.process.VerifiedUserCache;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AntiSpamServiceTest {

    @Mock
    private VerifiedUserCache verifiedUserCache;

    @Test
    @DisplayName("Должен вернуть false для проверенного пользователя.")
    protected void shouldReturnTrueCauseVerified() {
        AntiSpamService antiSpamService = new AntiSpamService(3, verifiedUserCache);
        Long chatId = 123_456_789L;
        when(verifiedUserCache.check(chatId)).thenReturn(true);
        assertFalse(() -> antiSpamService.isSpam(chatId));
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    @DisplayName("Должен вернуть false для первых трех вызовов, далее true.")
    protected void isSpamSameChatId() {
        Long chatId = 123_456_789L;
        AntiSpamService antiSpamService = new AntiSpamService(3, verifiedUserCache);
        for (int i = 0; i < 10; i++) {
            if (i < 3) {
                assertFalse(antiSpamService.isSpam(chatId));
            } else {
                assertTrue(antiSpamService.isSpam(chatId));
            }
        }
    }

    @Test
    @DisplayName("Должен вернуть false для первого вызова каждого chat id.")
    protected void isSpamDifferentChatId() {
        AntiSpamService antiSpamService = new AntiSpamService(3, verifiedUserCache);
        long startChatId = 100_000_000L;
        for (int i = 0; i < 10; i++) {
            assertFalse(antiSpamService.isSpam(startChatId++));
        }
    }

}