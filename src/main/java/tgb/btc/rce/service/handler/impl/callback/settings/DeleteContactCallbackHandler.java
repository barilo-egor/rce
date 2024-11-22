package tgb.btc.rce.service.handler.impl.callback.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.interfaces.service.bean.bot.IContactService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class DeleteContactCallbackHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final IContactService contactService;

    private final ICallbackDataService callbackDataService;

    public DeleteContactCallbackHandler(IResponseSender responseSender, IContactService contactService,
                                        ICallbackDataService callbackDataService) {
        this.responseSender = responseSender;
        this.contactService = contactService;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long pid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        String contactLabel = contactService.findById(pid).getLabel();
        contactService.deleteById(pid);
        Long chatId = callbackQuery.getFrom().getId();
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Контакт <b>" + contactLabel + "</b> успешно удален.");
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.DELETE_CONTACT;
    }
}
