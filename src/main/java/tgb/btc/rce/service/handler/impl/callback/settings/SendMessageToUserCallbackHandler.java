package tgb.btc.rce.service.handler.impl.callback.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.enums.BotSystemMessage;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class SendMessageToUserCallbackHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IResponseSender responseSender;

    public SendMessageToUserCallbackHandler(ICallbackDataService callbackDataService, IResponseSender responseSender) {
        this.callbackDataService = callbackDataService;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        String text = callbackQuery.getMessage().getText();
        Long userChatId = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        responseSender.sendMessage(userChatId, text);
        responseSender.sendEditedMessageText(chatId, callbackQuery.getMessage().getMessageId(),
                text + BotSystemMessage.MESSAGE_SENT.getText());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.SEND_MESSAGE_TO_USER;
    }
}
