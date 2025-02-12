package tgb.btc.rce.service.handler.impl.callback.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AutoWithdrawalDealHandler implements ICallbackQueryHandler {

    private final IReadDealService readDealService;

    private final ICryptoWithdrawalService cryptoWithdrawalService;

    private final ICallbackDataService callbackDataService;

    private final IResponseSender responseSender;

    public AutoWithdrawalDealHandler(IReadDealService readDealService,
                                     ICryptoWithdrawalService cryptoWithdrawalService, ICallbackDataService callbackDataService,
                                     IResponseSender responseSender) {
        this.readDealService = readDealService;
        this.cryptoWithdrawalService = cryptoWithdrawalService;
        this.callbackDataService = callbackDataService;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long dealPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        boolean withCommission = callbackDataService.getBoolArgument(callbackQuery.getData(), 2);
        Deal deal = readDealService.findByPid(dealPid);
        Long chatId = callbackQuery.getFrom().getId();
        Optional<Message> gettingBalanceMessage = responseSender.sendMessage(chatId, "Получение баланса.");
        BigDecimal balance = cryptoWithdrawalService.getBalance(deal.getCryptoCurrency());
        gettingBalanceMessage.ifPresent(msg -> responseSender.deleteMessage(chatId, msg.getMessageId()));
        if (balance.compareTo(deal.getCryptoAmount()) < 0) {
            responseSender.sendAnswerCallbackQuery(callbackQuery.getId(),
                    "На балансе недостаточно средств для автовывода. Текущий баланс: " + balance.toPlainString(), true);
            return;
        }
        sendConfirmMessage(withCommission, deal, chatId, callbackQuery.getMessage().getMessageId());
    }

    public void sendConfirmMessage(boolean withCommission, Deal deal, Long chatId, Integer messageId) {
        List<InlineButton> buttons = new ArrayList<>();
        if (withCommission) {
            buttons.add(InlineButton.builder()
                    .text("Изменить комиссию")
                    .data(callbackDataService.buildData(CallbackQueryData.DEAL_CHANGE_FEE_RATE,
                            deal.getPid(),
                            messageId))
                    .build());
        }
        buttons.add(InlineButton.builder()
                .text("Продолжить")
                .data(callbackDataService.buildData(
                        CallbackQueryData.CONFIRM_AUTO_WITHDRAWAL_DEAL,
                        deal.getPid(),
                        messageId,
                        withCommission
                )).build());
        buttons.add(InlineButton.builder().text("Отмена").data(CallbackQueryData.INLINE_DELETE.name()).build());
        responseSender.sendMessage(chatId, getMessage(withCommission, deal), buttons);
    }

    private String getMessage(boolean withCommission, Deal deal) {
        String message = "";
        if (withCommission) {
            String feeRate = cryptoWithdrawalService.getFeeRate(deal.getCryptoCurrency());
            if (cryptoWithdrawalService.isAutoFeeRate(deal.getCryptoCurrency())) {
                message = "Комиссия: <b>" + feeRate + "</b>\n";
            } else {
                message = "Комиссия: <b>" + feeRate + " sat/vB</b>\n";
            }
        }
        message = message + "Вы собираетесь отправить " + deal.getCryptoAmount().toPlainString()
                + " " + deal.getCryptoCurrency().getShortName() + " на адрес <code>" + deal.getWallet() + "</code>. Продолжить?";
        return message;
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.AUTO_WITHDRAWAL_DEAL;
    }
}
