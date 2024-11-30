package tgb.btc.rce.service.handler.impl.callback.request.api;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.web.ApiDealStatus;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.process.IApiDealBotService;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class ShowApiDealHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IApiDealService apiDealService;

    private final IApiDealBotService apiDealBotService;

    public ShowApiDealHandler(IResponseSender responseSender, ICallbackDataService callbackDataService,
                              IApiDealService apiDealService, IApiDealBotService apiDealBotService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.apiDealService = apiDealService;
        this.apiDealBotService = apiDealBotService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        Long pid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        ApiDealStatus status = apiDealService.getApiDealStatusByPid(pid);
        if (!ApiDealStatus.PAID.equals(status)) {
            responseSender.sendMessage(chatId, "Заявка уже обработана, либо отменена.");
            return;
        }
        apiDealBotService.sendApiDeal(pid, chatId);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.SHOW_API_DEAL;
    }
}
