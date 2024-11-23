package tgb.btc.rce.service.handler.impl.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;

@Service
public class SendMessageChatIdHandler implements IStateHandler {

    private final IResponseSender responseSender;

    private final IRedisStringService redisStringService;

    private final IRedisUserStateService redisUserStateService;

    private final IAdminPanelService adminPanelService;

    public SendMessageChatIdHandler(IResponseSender responseSender, IRedisStringService redisStringService,
                                    IRedisUserStateService redisUserStateService, IAdminPanelService adminPanelService) {
        this.responseSender = responseSender;
        this.redisStringService = redisStringService;
        this.redisUserStateService = redisUserStateService;
        this.adminPanelService = adminPanelService;
    }


    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update),
                    "Введите chat id пользователя, которому хотите отправить сообщение, либо нажмите \"" + TextCommand.CANCEL.getText() + "\".");
            return;
        }
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        if (text.equals(TextCommand.CANCEL.getText())) {
            redisUserStateService.delete(chatId);
            adminPanelService.send(chatId);
            return;
        }
        long userChatId;
        try {
            userChatId = Long.parseLong(text);
        } catch (NumberFormatException e) {
            responseSender.sendMessage(chatId, "Введите валидный chat id.");
            return;
        }
        redisStringService.save(chatId, Long.toString(userChatId));
        redisUserStateService.save(chatId, UserState.SEND_MESSAGE_TEXT);
        responseSender.sendMessage(chatId, "Введите текст сообщения, которое хотите отправить пользователю");
    }

    @Override
    public UserState getUserState() {
        return UserState.SEND_MESSAGE_CHAT_ID;
    }
}
