package tgb.btc.rce.service.handler.impl.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IStartService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class ShareReviewStateHandler implements IStateHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IRedisStringService redisStringService;

    private final IRedisUserStateService redisUserStateService;

    private final IStartService startService;

    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update), "Введите ваш отзыв, либо нажмите \""
                    + TextCommand.CANCEL.getText() + "\".");
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        if (message.getText().equals(TextCommand.CANCEL.getText())) {
            redisStringService.delete(chatId);
            redisUserStateService.delete(chatId);
            startService.processToMainMenu(chatId);
            return;
        }
        String text = message.getText();
        Long minSum
    }

    @Override
    public UserState getUserState() {
        return UserState.SHARE_REVIEW;
    }
}
