package tgb.btc.rce.service.handler.impl.state.users;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IUserInputService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;

import java.util.Objects;

@Service
public class SendMessageChatIdHandler implements IStateHandler {

    private final IResponseSender responseSender;

    private final IRedisStringService redisStringService;

    private final IRedisUserStateService redisUserStateService;

    private final IUserInputService userInputService;

    public SendMessageChatIdHandler(IResponseSender responseSender, IRedisStringService redisStringService,
                                    IRedisUserStateService redisUserStateService,
                                    IUserInputService userInputService) {
        this.responseSender = responseSender;
        this.redisStringService = redisStringService;
        this.redisUserStateService = redisUserStateService;
        this.userInputService = userInputService;
    }


    @Override
    public void handle(Update update) {
        if (!userInputService.hasTextInput(update)) {
            return;
        }
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        Long userChatId = userInputService.getInputChatId(chatId, text);
        if (Objects.isNull(userChatId)) {
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
