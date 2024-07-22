package tgb.btc.rce.service.processors.admin.requests.apideal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.api.web.INotifier;
import tgb.btc.library.constants.enums.web.ApiDealStatus;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;


@CommandProcessor(command = Command.CONFIRM_API_DEAL)
@Slf4j
public class ConfirmApiDeal extends Processor {

    private IApiDealService apiDealService;

    private IGroupChatService groupChatService;

    private INotifier notifier;

    @Autowired
    public void setNotifier(INotifier notifier) {
        this.notifier = notifier;
    }

    @Autowired
    public void setGroupChatService(IGroupChatService groupChatService) {
        this.groupChatService = groupChatService;
    }

    @Autowired
    public void setApiDealService(IApiDealService apiDealService) {
        this.apiDealService = apiDealService;
    }

    // TODO проверить сначала, подтверждена ли уже сделка
    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        boolean isNeedRequest = callbackQueryService.getSplitBooleanData(update, 2);
        Long dealPid = callbackQueryService.getSplitLongData(update, 1);
        if (isNeedRequest && !groupChatService.hasGroupChat(apiDealService.getApiUserPidByDealPid(dealPid))) {
            responseSender.sendAnswerCallbackQuery(update.getCallbackQuery().getId(),
                    "Не найдена установленная группа для вывода запросов этого клиента. " +
                            "Добавьте бота в группу, выдайте разрешения на отправку сообщений и выберите группу на сайте в " +
                            "разделе \"API пользователи\".\n", true);
            return;
        }
        responseSender.deleteMessage(chatId, callbackQueryService.messageId(update));
        apiDealService.updateApiDealStatusByPid(ApiDealStatus.ACCEPTED, dealPid);
        if (isNeedRequest)
            notifier.sendRequestToWithdrawApiDeal(dealPid);
        log.debug("Админ chatId={} подтвердил АПИ сделку={}.", chatId, dealPid);
        responseSender.sendMessage(chatId, "API сделка подтверждена.");
    }
}
