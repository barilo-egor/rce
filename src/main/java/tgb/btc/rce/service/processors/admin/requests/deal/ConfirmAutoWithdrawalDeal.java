package tgb.btc.rce.service.processors.admin.requests.deal;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.IAutoWithdrawalService;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.web.Notifier;

@CommandProcessor(command = Command.CONFIRM_AUTO_WITHDRAWAL_DEAL)
@Slf4j
public class ConfirmAutoWithdrawalDeal extends Processor {

    private final IAutoWithdrawalService autoWithdrawalService;

    private final IGroupChatService groupChatService;

    private final IModifyDealService modifyDealService;

    private final Notifier notifier;

    @Autowired
    public ConfirmAutoWithdrawalDeal(IAutoWithdrawalService autoWithdrawalService, IGroupChatService groupChatService,
                                     IModifyDealService modifyDealService, Notifier notifier) {
        this.autoWithdrawalService = autoWithdrawalService;
        this.groupChatService = groupChatService;
        this.modifyDealService = modifyDealService;
        this.notifier = notifier;
    }

    @Override
    public void run(Update update) {
        if (!update.hasCallbackQuery()) return;
        Long chatId = updateService.getChatId(update);
        Long dealPid = callbackQueryService.getSplitLongData(update, 1);
        if (!groupChatService.hasAutoWithdrawal()) {
            responseSender.sendAnswerCallbackQuery(update.getCallbackQuery().getId(),
                    "Не найдена установленная группа для автовывода сделок. " +
                            "Добавьте бота в группу, выдайте разрешения на отправку сообщений и выберите группу на сайте в " +
                            "разделе \"Сделки из бота\".\n", true);
            return;
        }
        autoWithdrawalService.withdrawal(dealPid);
        modifyDealService.confirm(dealPid);
        String username = readUserService.getUsernameByChatId(chatId);
        log.debug("Админ {} подтвердил сделку {} с автовыводом.", chatId, dealPid);
        log.debug("Сделка {} была отправлена в группу автовывода сделок.", dealPid);
        notifier.sendAutoWithdrawDeal(
                "бота",
                StringUtils.isNotEmpty(username)
                        ? username
                        : "chatid:" + chatId,
                dealPid);
        responseSender.deleteMessage(chatId, callbackQueryService.getSplitIntData(update, 2));
        responseSender.deleteMessage(chatId, updateService.getMessage(update).getMessageId());
    }
}
