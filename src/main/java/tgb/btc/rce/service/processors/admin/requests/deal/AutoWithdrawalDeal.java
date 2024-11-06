package tgb.btc.rce.service.processors.admin.requests.deal;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.util.ICallbackQueryService;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.util.Optional;

@CommandProcessor(command = Command.AUTO_WITHDRAWAL_DEAL)
public class AutoWithdrawalDeal extends Processor {

    private final ICallbackQueryService callbackQueryService;

    private final IReadDealService readDealService;

    private final ICryptoWithdrawalService cryptoWithdrawalService;

    @Autowired
    public AutoWithdrawalDeal(ICallbackQueryService callbackQueryService, IReadDealService readDealService,
                              ICryptoWithdrawalService cryptoWithdrawalService) {
        this.callbackQueryService = callbackQueryService;
        this.readDealService = readDealService;
        this.cryptoWithdrawalService = cryptoWithdrawalService;
    }

    @Override
    public void run(Update update) {
        Long dealPid = callbackQueryService.getSplitLongData(update, 1);
        Deal deal = readDealService.findByPid(dealPid);
        Long chatId = updateService.getChatId(update);
        Optional<Message> gettingBalanceMessage = responseSender.sendMessage(chatId, "Получение баланса.");
        BigDecimal balance = cryptoWithdrawalService.getBalance(deal.getCryptoCurrency());
        gettingBalanceMessage.ifPresent(msg -> responseSender.deleteMessage(chatId, msg.getMessageId()));
        if (balance.compareTo(deal.getCryptoAmount()) < 0) {
            responseSender.sendAnswerCallbackQuery(update.getCallbackQuery().getId(),
                    "На балансе недостаточно средств для автовывода. Текущий баланс: " + balance.toPlainString(), true);
            return;
        }
        String message = "Вы собираетесь отправить " + deal.getCryptoAmount().toPlainString()
                + " " + deal.getCryptoCurrency().getShortName() + " на адрес <code>" + deal.getWallet() + "</code>. Продолжить?";
        responseSender.sendMessage(chatId, message, "html",
                InlineButton.builder().text(Command.CONFIRM_AUTO_WITHDRAWAL_DEAL.getText())
                        .data(callbackQueryService.buildCallbackData(Command.CONFIRM_AUTO_WITHDRAWAL_DEAL,
                                new Object[]{dealPid, callbackQueryService.messageId(update)})).build(),
                InlineButton.builder().text("Отмена").data(Command.INLINE_DELETE.name()).build());
    }
}
