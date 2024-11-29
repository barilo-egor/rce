package tgb.btc.rce.service.handler.impl.callback.request;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.api.web.INotifier;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.handler.util.IStartService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
@Slf4j
public class ConfirmUserDealHandler implements ICallbackQueryHandler {

    private final IModifyDealService modifyDealService;

    private final INotifier notifier;

    private final IGroupChatService groupChatService;

    private final IDealUserService dealUserService;

    private final ICryptoWithdrawalService cryptoWithdrawalService;

    private final ICallbackDataService callbackDataService;

    private final IResponseSender responseSender;

    private final IStartService startService;

    private final IReadUserService readUserService;

    private final IRedisUserStateService redisUserStateService;

    @Value("${bot.username}")
    private String botUsername;

    public ConfirmUserDealHandler(IModifyDealService modifyDealService, INotifier notifier,
                                  IGroupChatService groupChatService, IDealUserService dealUserService,
                                  ICryptoWithdrawalService cryptoWithdrawalService,
                                  ICallbackDataService callbackDataService, IResponseSender responseSender,
                                  IStartService startService, IReadUserService readUserService,
                                  IRedisUserStateService redisUserStateService) {
        this.modifyDealService = modifyDealService;
        this.notifier = notifier;
        this.groupChatService = groupChatService;
        this.dealUserService = dealUserService;
        this.cryptoWithdrawalService = cryptoWithdrawalService;
        this.callbackDataService = callbackDataService;
        this.responseSender = responseSender;
        this.startService = startService;
        this.readUserService = readUserService;
        this.redisUserStateService = redisUserStateService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long dealPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        boolean isNeedRequest = Boolean.parseBoolean(callbackDataService.getArgument(callbackQuery.getData(), 2));
        if (isNeedRequest && !groupChatService.hasDealRequests()) {
            responseSender.sendAnswerCallbackQuery(callbackQuery.getId(),
                    "Не найдена установленная группа для вывода запросов. " +
                            "Добавьте бота в группу, выдайте разрешения на отправку сообщений и выберите группу на сайте в " +
                            "разделе \"Сделки из бота\".\n", true);
            return;
        }
        modifyDealService.confirm(dealPid);
        new Thread(() -> cryptoWithdrawalService.deleteFromPool(botUsername, dealPid)).start();
        Long userChatId = dealUserService.getUserChatIdByDealPid(dealPid);
        if (UserState.ADDITIONAL_VERIFICATION.equals(redisUserStateService.get(userChatId))) {
            startService.process(userChatId);
        }
        String username = readUserService.getUsernameByChatId(chatId);
        log.debug("Админ {} подтвердил сделку {}.", chatId, dealPid);
        if (isNeedRequest) {
            log.debug("Сделка {} была отправлена в группу запросов.", dealPid);
            notifier.sendRequestToWithdrawDeal(
                    "бота",
                    StringUtils.isNotEmpty(username)
                            ? username
                            : "chatid:" + chatId,
                    dealPid);
        }
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.CONFIRM_USER_DEAL;
    }
}
