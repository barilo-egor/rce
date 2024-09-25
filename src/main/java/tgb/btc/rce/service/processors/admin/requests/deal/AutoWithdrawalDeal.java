package tgb.btc.rce.service.processors.admin.requests.deal;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.interfaces.service.IAutoWithdrawalService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.util.ICallbackQueryService;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;

@CommandProcessor(command = Command.AUTO_WITHDRAWAL_DEAL)
public class AutoWithdrawalDeal extends Processor {

    private final IAutoWithdrawalService autoWithdrawalService;

    private final ICallbackQueryService callbackQueryService;

    private final IReadDealService readDealService;

    @Autowired
    public AutoWithdrawalDeal(IAutoWithdrawalService autoWithdrawalService, ICallbackQueryService callbackQueryService, IReadDealService readDealService) {
        this.autoWithdrawalService = autoWithdrawalService;
        this.callbackQueryService = callbackQueryService;
        this.readDealService = readDealService;
    }

    @Override
    public void run(Update update) {
        Long dealPid = callbackQueryService.getSplitLongData(update, 1);
        Deal deal = readDealService.findByPid(dealPid);
        BigDecimal balance = autoWithdrawalService.getBalance(deal.getCryptoCurrency());
        if (balance.compareTo(deal.getCryptoAmount()) < 0) {
            responseSender.sendAnswerCallbackQuery(update.getCallbackQuery().getId(),
                    "На балансе недостаточно средств для автовывода. Текущий баланс: " + balance.toPlainString(), true);
            return;
        }
        String message = "Вы собираетесь отправить " + deal.getCryptoAmount().toPlainString()
                + " " + deal.getCryptoCurrency().getShortName() + " на адрес <code>" + deal.getWallet() + "</code>. Продолжить?";
        responseSender.sendMessage(updateService.getChatId(update), message, "html",
                InlineButton.builder().text(Command.CONFIRM_AUTO_WITHDRAWAL_DEAL.getText())
                        .data(callbackQueryService.buildCallbackData(Command.CONFIRM_AUTO_WITHDRAWAL_DEAL,
                                new Object[]{dealPid, callbackQueryService.messageId(update)})).build(),
                InlineButton.builder().text("Отмена").data(Command.INLINE_DELETE.name()).build());
    }
}
