package tgb.btc.rce.service.processors.admin.requests.deal;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.api.web.INotifier;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealUserService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.util.ITelegramPropertiesService;

@CommandProcessor(command = Command.CONFIRM_USER_DEAL)
@Slf4j
public class ConfirmUserDeal extends Processor {

    private IModifyDealService modifyDealService;

    private INotifier notifier;

    private IGroupChatService groupChatService;

    private IDealUserService dealUserService;

    private ICryptoWithdrawalService cryptoWithdrawalService;

    private String botUsername;

    public ConfirmUserDeal(IModifyDealService modifyDealService, INotifier notifier, IGroupChatService groupChatService,
                           IDealUserService dealUserService, ICryptoWithdrawalService cryptoWithdrawalService,
                           ITelegramPropertiesService telegramPropertiesService) {
        this.modifyDealService = modifyDealService;
        this.notifier = notifier;
        this.groupChatService = groupChatService;
        this.dealUserService = dealUserService;
        this.cryptoWithdrawalService = cryptoWithdrawalService;
        this.botUsername = telegramPropertiesService.getUsername();
    }

    @Override
    @Transactional
    public void run(Update update) {
        if (!update.hasCallbackQuery()) return;
        Long chatId = updateService.getChatId(update);
        boolean isNeedRequest = callbackQueryService.getSplitBooleanData(update, 2);
        Long dealPid = callbackQueryService.getSplitLongData(update, 1);
        if (isNeedRequest && !groupChatService.hasDealRequests()) {
            responseSender.sendAnswerCallbackQuery(update.getCallbackQuery().getId(),
                    "Не найдена установленная группа для вывода запросов. " +
                            "Добавьте бота в группу, выдайте разрешения на отправку сообщений и выберите группу на сайте в " +
                            "разделе \"Сделки из бота\".\n", true);
            return;
        }
        modifyDealService.confirm(dealPid);
        new Thread(() -> cryptoWithdrawalService.deleteFromPool(botUsername, dealPid)).start();
        Long userChatId = dealUserService.getUserChatIdByDealPid(dealPid);
        if (Command.USER_ADDITIONAL_VERIFICATION.equals(Command.valueOf(readUserService.getCommandByChatId(userChatId)))) {
            processToMainMenu(userChatId);
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
        responseSender.deleteMessage(chatId, updateService.getMessage(update).getMessageId());
    }
}
