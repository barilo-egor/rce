package tgb.btc.rce.service.processors.admin.requests.deal;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealStatus;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.service.AutoWithdrawalService;
import tgb.btc.library.service.util.BigDecimalService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.util.List;

@CommandProcessor(command = Command.BITCOIN_POOL)
public class BitcoinPool extends Processor {

    private final BigDecimalService bigDecimalService;
    private final IReadDealService readDealService;
    private final AutoWithdrawalService autoWithdrawalService;

    @Autowired
    public BitcoinPool(IReadDealService readDealService, BigDecimalService bigDecimalService, AutoWithdrawalService autoWithdrawalService) {
        this.readDealService = readDealService;
        this.bigDecimalService = bigDecimalService;
        this.autoWithdrawalService = autoWithdrawalService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        List<Deal> deals = readDealService.getAllByDealStatusAndCryptoCurrency(DealStatus.AWAITING_WITHDRAWAL, CryptoCurrency.BITCOIN);
        if (CollectionUtils.isEmpty(deals)) {
            responseSender.sendMessage(chatId, "Текущий пул сделок BTC пуст.");
            return;
        }
        BigDecimal totalAmount = BigDecimal.ZERO;
        StringBuilder dealsInfo = new StringBuilder();
        dealsInfo.append("Текущий пул сделок BTC:\n");
        for (Deal deal : deals) {
            dealsInfo.append("-----------").append("\n")
                    .append("Сделка №").append(deal.getPid()).append("\n")
                    .append("-----------").append("\n")
                    .append("Адрес: <code>").append(deal.getWallet()).append("</code>").append("\n")
                    .append("Сумма: <code>")
                    .append(bigDecimalService.roundToPlainString(deal.getCryptoAmount(), CryptoCurrency.BITCOIN.getScale()))
                    .append("</code>\n")
                    .append("Для удаления сделки из пула введите <code>/deletefrompool ").append(deal.getPid()).append("</code>").append("\n")
                    .append("\n");
            totalAmount = totalAmount.add(deal.getCryptoAmount());
        }
        dealsInfo.append("-----------").append("\n");
        String strTotalAmount = bigDecimalService.roundToPlainString(totalAmount, CryptoCurrency.BITCOIN.getScale());
        dealsInfo.append("Общая сумма: <code>").append(bigDecimalService.roundToPlainString(totalAmount, CryptoCurrency.BITCOIN.getScale())).append("</code>\n");
        dealsInfo.append("Баланс кошелька: <code>").append(autoWithdrawalService.getBalance(CryptoCurrency.BITCOIN)).append("</code>");
        ReplyKeyboard replyKeyboard = keyboardBuildService.buildInline(
                List.of(
                        InlineButton.builder()
                                .text(commandService.getText(Command.BITCOIN_POOL_WITHDRAWAL))
                                .data(callbackQueryService.buildCallbackData(Command.BITCOIN_POOL_WITHDRAWAL, new Object[]{deals.size(), strTotalAmount}))
                                .build(),
                        InlineButton.builder()
                                .text(commandService.getText(Command.CLEAR_POOL))
                                .data(callbackQueryService.buildCallbackData(Command.CLEAR_POOL, deals.size()))
                                .build(),
                        InlineButton.builder().text(commandService.getText(Command.INLINE_DELETE)).data(Command.INLINE_DELETE.name()).build()
                ), 2);
        responseSender.sendMessage(chatId, dealsInfo.toString(), replyKeyboard, "html");
    }
}
