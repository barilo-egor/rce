package tgb.btc.rce.service.handler.impl.callback.request;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealStatus;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.impl.web.Notifier;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.Optional;

@Service
@Slf4j
public class ConfirmAutoWithdrawalDealHandler implements ICallbackQueryHandler {

    private final IGroupChatService groupChatService;

    private final IModifyDealService modifyDealService;

    private final Notifier notifier;

    private final IReadDealService readDealService;

    private final ICryptoWithdrawalService cryptoWithdrawalService;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IReadUserService readUserService;

    @Value("${bot.username}")
    private String botUsername;

    public ConfirmAutoWithdrawalDealHandler(IGroupChatService groupChatService, IModifyDealService modifyDealService,
                                            Notifier notifier, IReadDealService readDealService,
                                            ICryptoWithdrawalService cryptoWithdrawalService,
                                            IResponseSender responseSender, ICallbackDataService callbackDataService,
                                            IReadUserService readUserService) {
        this.groupChatService = groupChatService;
        this.modifyDealService = modifyDealService;
        this.notifier = notifier;
        this.readDealService = readDealService;
        this.cryptoWithdrawalService = cryptoWithdrawalService;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.readUserService = readUserService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long dealPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        if (!groupChatService.hasAutoWithdrawal()) {
            responseSender.sendAnswerCallbackQuery(callbackQuery.getId(),
                    "Не найдена установленная группа для автовывода сделок. " +
                            "Добавьте бота в группу, выдайте разрешения на отправку сообщений и выберите группу на сайте в " +
                            "разделе \"Сделки из бота\".\n", true);
            return;
        }
        Optional<Message> withdrawalMessage = responseSender.sendMessage(chatId, "Автовывод в процессе, пожалуйста подождите.");;
        String hash;
        try {
            Deal deal = readDealService.findByPid(dealPid);
            if (DealStatus.CONFIRMED.equals(deal.getDealStatus())) {
                responseSender.sendMessage(chatId, "Сделка уже находится в статусе \"Подтверждена\".");
                return;
            }
            hash = cryptoWithdrawalService.withdrawal(deal.getCryptoCurrency(), deal.getCryptoAmount(), deal.getWallet());
        } catch (Exception e) {
            responseSender.sendMessage(chatId, "Ошибка при попытке автовывода сделки " + dealPid + ": " + e.getMessage());
            return;
        } finally {
            withdrawalMessage.ifPresent(msg -> responseSender.deleteMessage(chatId, msg.getMessageId()));
        }
        modifyDealService.confirm(dealPid, hash);
        new Thread(() -> cryptoWithdrawalService.deleteFromPool(botUsername, dealPid)).start();
        String username = readUserService.getUsernameByChatId(chatId);
        log.debug("Админ {} подтвердил сделку {} с автовыводом. Хеш транзакции: {}", chatId, dealPid, hash);
        notifier.sendAutoWithdrawDeal(
                "бота",
                StringUtils.isNotEmpty(username)
                        ? username
                        : "chatid:" + chatId,
                dealPid);
        log.debug("Сделка {} была отправлена в группу автовывода сделок.", dealPid);
        responseSender.deleteMessage(chatId, callbackDataService.getIntArgument(callbackQuery.getData(), 2));
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Транзакция сделки №" + dealPid + "\n" + String.format(CryptoCurrency.BITCOIN.getHashUrl(), hash));
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.CONFIRM_AUTO_WITHDRAWAL_DEAL;
    }
}
