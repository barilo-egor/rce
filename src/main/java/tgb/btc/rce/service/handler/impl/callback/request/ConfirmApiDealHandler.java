package tgb.btc.rce.service.handler.impl.callback.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.api.web.INotifier;
import tgb.btc.library.constants.enums.ApiDealType;
import tgb.btc.library.constants.enums.web.ApiDealStatus;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
@Slf4j
public class ConfirmApiDealHandler implements ICallbackQueryHandler {

    private final IApiDealService apiDealService;

    private final IGroupChatService groupChatService;

    private final INotifier notifier;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    public ConfirmApiDealHandler(IApiDealService apiDealService, IGroupChatService groupChatService,
                                 INotifier notifier, IResponseSender responseSender,
                                 ICallbackDataService callbackDataService) {
        this.apiDealService = apiDealService;
        this.groupChatService = groupChatService;
        this.notifier = notifier;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long dealPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        boolean isNeedRequest = callbackDataService.getBoolArgument(callbackQuery.getData(), 2);
        if (isNeedRequest && !groupChatService.hasGroupChat(apiDealService.getApiUserPidByDealPid(dealPid))) {
            responseSender.sendAnswerCallbackQuery(callbackQuery.getId(),
                    "Не найдена установленная группа для вывода запросов этого клиента. " +
                            "Добавьте бота в группу, выдайте разрешения на отправку сообщений и выберите группу на сайте в " +
                            "разделе \"API пользователи\".\n", true);
            return;
        }
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        apiDealService.updateApiDealStatusByPid(ApiDealStatus.ACCEPTED, dealPid);
        if (isNeedRequest)
            notifier.sendRequestToWithdrawApiDeal(dealPid);
        log.debug("Админ chatId={} подтвердил АПИ сделку={}.", chatId, dealPid);
        String message = ApiDealType.API.equals(apiDealService.getApiDealTypeByPid(dealPid))
                ? "API сделка подтверждена."
                : "Диспут подтвержден.";
        responseSender.sendMessage(chatId, message);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.CONFIRM_API_DEAL;
    }
}
