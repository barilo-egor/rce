package tgb.btc.rce.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.event.ContextClosedEvent;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.service.INotifyService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContextClosedListenerTest {

    @Mock
    private INotifyService notifyService;

    @Mock
    private ContextClosedEvent event;

    @InjectMocks
    private ContextClosedListener contextClosedListener;

    @Test
    protected void shouldSkipIfNull() {
        contextClosedListener.onApplicationEvent(null);
        verify(notifyService, times(0)).notifyMessage(anyString(), any(), anyBoolean());
    }

    @Test
    protected void shouldNotify() {
        contextClosedListener.onApplicationEvent(event);
        verify(notifyService, times(1)).notifyMessage("⚠️ Рестарт бота.", UserRole.OPERATOR_ACCESS, false);
    }

}