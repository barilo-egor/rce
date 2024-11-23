package tgb.btc.rce.service.handler.impl.message.text.command.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.redis.IRedisUserStateService;

@Service
public class DealsCountHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final IKeyboardService keyboardService;

    private final IRedisUserStateService redisUserStateService;

    public DealsCountHandler(IResponseSender responseSender, IKeyboardService keyboardService,
                             IRedisUserStateService redisUserStateService) {
        this.responseSender = responseSender;
        this.keyboardService = keyboardService;
        this.redisUserStateService = redisUserStateService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        responseSender.sendMessage(
                chatId,
                "Введите новое значение для количества возможных активных сделок.",
                keyboardService.getReplyCancel()
        );
        redisUserStateService.save(chatId, UserState.DEALS_COUNT);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.DEALS_COUNT;
    }
}
