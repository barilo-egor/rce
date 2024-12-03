package tgb.btc.rce.service.handler.impl.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.UpdateFilterType;
import tgb.btc.rce.service.handler.util.IStartService;
import tgb.btc.rce.service.process.IUserProcessService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StartFilterTest {

    @Mock
    private IStartService startService;

    @Mock
    private IUserProcessService userProcessService;

    @InjectMocks
    private StartFilter startFilter;

    @Test
    protected void handle() {
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        Long chatId = 123456789L;
        chat.setId(chatId);
        message.setChat(chat);
        update.setMessage(message);
        startFilter.handle(update);
        verify(userProcessService, times(1)).registerIfNotExists(update);
        verify(startService, times(1)).process(chatId);
    }

    @Test
    protected void getType() {
        assertEquals(UpdateFilterType.START, startFilter.getType());
    }

}