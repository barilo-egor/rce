package tgb.btc.rce.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.service.INotifyService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContextStartedListenerTest {

    @Mock
    private INotifyService notifyService;

    @Mock
    private ApplicationReadyEvent applicationReadyEvent;

    @InjectMocks
    private ContextStartedListener listener;

    @Test
    protected void onApplicationEvent() {
        listener.onApplicationEvent(applicationReadyEvent);
        verify(notifyService, times(1)).notifyMessage("\uD83D\uDFE2 Бот запущен.", UserRole.OPERATOR_ACCESS, false);
    }
}