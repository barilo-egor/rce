package tgb.btc.rce.service.handler.impl.callback.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.enums.BotSystemMessage;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.processors.support.MessagesService;

@Service
public class MailingListCallbackHandler implements ICallbackQueryHandler {

    private final MessagesService messagesService;

    private final IResponseSender responseSender;

    public MailingListCallbackHandler(MessagesService messagesService, IResponseSender responseSender) {
        this.messagesService = messagesService;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        String text = callbackQuery.getMessage().getText();
        messagesService.sendMessageToUsers(chatId, callbackQuery.getMessage().getText());
        responseSender.sendMessage(chatId, "Рассылка запущена. По окончанию вам придет оповещение.");
        responseSender.sendEditedMessageText(chatId, callbackQuery.getMessage().getMessageId(),
                text + BotSystemMessage.MESSAGE_SENT.getText());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.MAILING_LIST;
    }
}
