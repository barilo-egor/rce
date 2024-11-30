package tgb.btc.rce.service.handler.impl.message.text.command.users;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.redis.IRedisUserStateService;

@Service
public class MailingListHandler implements ITextCommandHandler {

    private final IRedisUserStateService redisUserStateService;

    private final IResponseSender responseSender;

    private final IKeyboardService keyboardService;

    public MailingListHandler(IRedisUserStateService redisUserStateService, IResponseSender responseSender,
                              IKeyboardService keyboardService) {
        this.redisUserStateService = redisUserStateService;
        this.responseSender = responseSender;
        this.keyboardService = keyboardService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        redisUserStateService.save(chatId, UserState.MAILING_LIST);
        responseSender.sendMessage(chatId, "Введите текст сообщения для рассылки.", keyboardService.getReplyCancel());
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.MAILING_LIST;
    }
}
