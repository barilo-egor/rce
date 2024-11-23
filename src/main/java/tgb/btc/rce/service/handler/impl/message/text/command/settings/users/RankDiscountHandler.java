package tgb.btc.rce.service.handler.impl.message.text.command.settings.users;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.redis.IRedisUserStateService;

@Service
public class RankDiscountHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final IRedisUserStateService redisUserStateService;

    private final IKeyboardService keyboardService;

    public RankDiscountHandler(IResponseSender responseSender, IRedisUserStateService redisUserStateService,
                               IKeyboardService keyboardService) {
        this.responseSender = responseSender;
        this.redisUserStateService = redisUserStateService;
        this.keyboardService = keyboardService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        responseSender.sendMessage(
                chatId,
                "Введите chat id пользователя для включения/выключения реферальной скидки.",
                keyboardService.getReplyCancel()
        );
        redisUserStateService.save(chatId, UserState.RANK_DISCOUNT);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.RANK_DISCOUNT;
    }
}
