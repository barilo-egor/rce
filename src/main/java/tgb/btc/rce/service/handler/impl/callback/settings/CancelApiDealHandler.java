package tgb.btc.rce.service.handler.impl.callback.settings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.ApiDealType;
import tgb.btc.library.constants.enums.web.ApiDealStatus;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
@Slf4j
public class CancelApiDealHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IApiDealService apiDealService;

    public CancelApiDealHandler(IResponseSender responseSender, ICallbackDataService callbackDataService,
                                IApiDealService apiDealService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.apiDealService = apiDealService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        Long dealPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        apiDealService.updateApiDealStatusByPid(ApiDealStatus.DECLINED, dealPid);
        log.debug("Админ chatId={} отменил АПИ сделку={}.", chatId, dealPid);
        String message = ApiDealType.API.equals(apiDealService.getApiDealTypeByPid(dealPid))
                ? "API сделка отменена."
                : "Диспут отменен.";
        responseSender.sendMessage(chatId, message);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.CANCEL_API_DEAL;
    }
}
