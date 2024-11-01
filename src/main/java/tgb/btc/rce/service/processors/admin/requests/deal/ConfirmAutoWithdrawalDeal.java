package tgb.btc.rce.service.processors.admin.requests.deal;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.constants.enums.bot.DealStatus;
import tgb.btc.library.interfaces.service.IAutoWithdrawalService;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.web.Notifier;

import java.util.Optional;

@CommandProcessor(command = Command.CONFIRM_AUTO_WITHDRAWAL_DEAL)
@Slf4j
public class ConfirmAutoWithdrawalDeal extends Processor {

    private final IAutoWithdrawalService autoWithdrawalService;

    private final IGroupChatService groupChatService;

    private final IModifyDealService modifyDealService;

    private final Notifier notifier;

    private final IReadDealService readDealService;

    private final ICryptoWithdrawalService cryptoWithdrawalService;

    @Autowired
    public ConfirmAutoWithdrawalDeal(IAutoWithdrawalService autoWithdrawalService, IGroupChatService groupChatService,
                                     IModifyDealService modifyDealService, Notifier notifier,
                                     IReadDealService readDealService,
                                     ICryptoWithdrawalService cryptoWithdrawalService) {
        this.autoWithdrawalService = autoWithdrawalService;
        this.groupChatService = groupChatService;
        this.modifyDealService = modifyDealService;
        this.notifier = notifier;
        this.readDealService = readDealService;
        this.cryptoWithdrawalService = cryptoWithdrawalService;
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
        Optional<Message> withdrawalMessage;
        String hash;
        try {
            Deal deal = readDealService.findByPid(dealPid);
            if (DealStatus.CONFIRMED.equals(deal.getDealStatus())) {
                responseSender.sendMessage(chatId, "Сделка уже находится в статусе \"Подтверждена\".");
                return;
            }
            withdrawalMessage = responseSender.sendMessage(chatId, "Автовывод в процессе, пожалуйста подождите.");
            hash = cryptoWithdrawalService.withdrawal(deal.getCryptoCurrency(), deal.getCryptoAmount(), deal.getWallet());
        } catch (Exception e) {
            responseSender.sendMessage(chatId, "Ошибка при попытке автовывода сделки " + dealPid + ": " + e.getMessage());
            return;
        }
        withdrawalMessage.ifPresent(msg -> responseSender.deleteMessage(chatId, msg.getMessageId()));

        modifyDealService.confirm(dealPid);
        String username = readUserService.getUsernameByChatId(chatId);
        log.debug("Админ {} подтвердил сделку {} с автовыводом. Хеш транзакции: {}", chatId, dealPid, hash);
        notifier.sendAutoWithdrawDeal(
                "бота",
                StringUtils.isNotEmpty(username)
                        ? username
                        : "chatid:" + chatId,
                dealPid);
        log.debug("Сделка {} была отправлена в группу автовывода сделок.", dealPid);
        responseSender.deleteMessage(chatId, callbackQueryService.getSplitIntData(update, 2));
        responseSender.deleteMessage(chatId, updateService.getMessage(update).getMessageId());
    }
}
