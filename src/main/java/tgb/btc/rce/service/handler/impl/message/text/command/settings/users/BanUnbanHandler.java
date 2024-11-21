package tgb.btc.rce.service.handler.impl.message.text.command.settings.users;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.IRedisUserStateService;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

@Service
public class BanUnbanHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final IKeyboardService keyboardService;

    private final IRedisUserStateService redisUserStateService;

    public BanUnbanHandler(IResponseSender responseSender, IKeyboardService keyboardService,
                           IRedisUserStateService redisUserStateService) {
        this.responseSender = responseSender;
        this.keyboardService = keyboardService;
        this.redisUserStateService = redisUserStateService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        redisUserStateService.save(chatId, UserState.BAN_UNBAN);
        responseSender.sendMessage(chatId, "Введите чат айди пользователя.", keyboardService.getReplyCancel());
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.BAN_UNBAN;
    }
}
