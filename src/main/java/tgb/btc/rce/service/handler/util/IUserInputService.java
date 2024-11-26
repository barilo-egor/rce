package tgb.btc.rce.service.handler.util;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface IUserInputService {
    boolean hasTextInput(Update update);

    Long getInputChatId(Long chatId, String input);
}
