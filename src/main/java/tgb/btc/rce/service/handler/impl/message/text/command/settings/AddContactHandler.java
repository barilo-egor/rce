package tgb.btc.rce.service.handler.impl.message.text.command.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.IMessagePropertiesService;

@Service
public class AddContactHandler implements ITextCommandHandler {

    private final IRedisUserStateService redisUserStateService;

    private final IResponseSender responseSender;

    private final IMessagePropertiesService messagePropertiesService;

    private final IKeyboardService keyboardService;

    public AddContactHandler(IRedisUserStateService redisUserStateService, IResponseSender responseSender,
                             IMessagePropertiesService messagePropertiesService, IKeyboardService keyboardService) {
        this.redisUserStateService = redisUserStateService;
        this.responseSender = responseSender;
        this.messagePropertiesService = messagePropertiesService;
        this.keyboardService = keyboardService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        redisUserStateService.save(chatId, UserState.ADD_CONTACT);
        responseSender.sendMessage(chatId, messagePropertiesService.getMessage(PropertiesMessage.CONTACT_ASK_INPUT),
                keyboardService.getReplyCancel());
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.ADD_CONTACT;
    }
}
