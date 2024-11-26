package tgb.btc.rce.service.handler.impl.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.Optional;

@Service
public class SendMessageTextHandler implements IStateHandler {

    private final IResponseSender responseSender;

    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;

    private final IAdminPanelService adminPanelService;

    private final ICallbackDataService callbackDataService;

    public SendMessageTextHandler(IResponseSender responseSender, IRedisUserStateService redisUserStateService,
                                  IRedisStringService redisStringService, IAdminPanelService adminPanelService,
                                  ICallbackDataService callbackDataService) {
        this.responseSender = responseSender;
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
        this.adminPanelService = adminPanelService;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update),
                    "Отправьте текст сообщения, либо нажмите \"" + TextCommand.CANCEL.getText() + "\".");
            return;
        }
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        if (TextCommand.CANCEL.getText().equals(text)) {
            redisUserStateService.delete(chatId);
            redisStringService.delete(chatId);
            adminPanelService.send(chatId);
            return;
        }
        Long userChatId = Long.parseLong(redisStringService.get(chatId));
        Optional<Message> message = responseSender.sendMessage(chatId, "Предварительный просмотр сообщения, отправляемого пользователю <b>"
                + userChatId + "</b>:");
        responseSender.sendMessage(chatId, text,
                InlineButton.builder()
                        .text("Отправить")
                        .data(callbackDataService.buildData(CallbackQueryData.SEND_MESSAGE_TO_USER, userChatId))
                        .build(),
                InlineButton.builder()
                        .text("Отмена")
                        .data(callbackDataService.buildData(CallbackQueryData.INLINE_DELETE, message.get().getMessageId()))
                        .build()
        );
        redisStringService.delete(chatId);
        redisUserStateService.delete(chatId);
        adminPanelService.send(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.SEND_MESSAGE_TEXT;
    }
}
