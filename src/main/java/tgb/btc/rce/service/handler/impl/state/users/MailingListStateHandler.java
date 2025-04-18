package tgb.btc.rce.service.handler.impl.state.users;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.BotReplyButton;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.Optional;

@Service
public class MailingListStateHandler implements IStateHandler {

    private final IResponseSender responseSender;

    private final IAdminPanelService adminPanelService;

    private final IRedisUserStateService redisUserStateService;

    private final ICallbackDataService callbackDataService;

    public MailingListStateHandler(IResponseSender responseSender, IAdminPanelService adminPanelService,
                                   IRedisUserStateService redisUserStateService, ICallbackDataService callbackDataService) {
        this.responseSender = responseSender;
        this.adminPanelService = adminPanelService;
        this.redisUserStateService = redisUserStateService;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update),
                    "Введите текст для рассылки, либо нажмите \"" + TextCommand.CANCEL + "\".");
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        if (message.getText().equals(BotReplyButton.CANCEL.getText())) {
            redisUserStateService.delete(chatId);
            adminPanelService.send(chatId);
        }
        Optional<Message> preMessage = responseSender.sendMessage(chatId, "Предварительный просмотр сообщения:");
        if (preMessage.isPresent()) {
            responseSender.sendMessage(message.getChatId(), message.getText(),
                    InlineButton.builder().text("Отправить пользователям").data(CallbackQueryData.MAILING_LIST.name()).build(),
                    InlineButton.builder().text("Отмена").data(callbackDataService.buildData(CallbackQueryData.INLINE_DELETE,
                            preMessage.get().getMessageId())).build());
            redisUserStateService.delete(chatId);
            adminPanelService.send(chatId);
        }
    }

    @Override
    public UserState getUserState() {
        return UserState.MAILING_LIST;
    }
}
