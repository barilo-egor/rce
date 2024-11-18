package tgb.btc.rce.service.processors.admin.requests.deal;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.exception.ApiResponseErrorException;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.library.service.util.BigDecimalService;
import tgb.btc.library.vo.web.PoolDeal;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@CommandProcessor(command = Command.BITCOIN_POOL)
public class BitcoinPool extends Processor {

    private final BigDecimalService bigDecimalService;
    private final ICryptoWithdrawalService cryptoWithdrawalService;

    @Autowired
    public BitcoinPool(BigDecimalService bigDecimalService, ICryptoWithdrawalService cryptoWithdrawalService) {
        this.bigDecimalService = bigDecimalService;
        this.cryptoWithdrawalService = cryptoWithdrawalService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        processPool(chatId);
    }

    public void processPool(Long chatId) {
        List<PoolDeal> deals;
        Optional<Message> poolMessage = responseSender.sendMessage(chatId, "Получение пула сделок BTC.");
        try {
            deals = cryptoWithdrawalService.getAllPoolDeals();
        } catch (ApiResponseErrorException e) {
            responseSender.sendMessage(chatId, e.getMessage());
            return;
        } finally {
            poolMessage.ifPresent(message -> responseSender.deleteMessage(chatId, message.getMessageId()));
        }
        Map<String, List<PoolDeal>> sortedByBotDeals = deals.stream()
                .collect(Collectors.groupingBy(PoolDeal::getBot, TreeMap::new, Collectors.toList()));
        if (CollectionUtils.isEmpty(deals)) {
            responseSender.sendMessage(chatId, "Текущий пул сделок BTC пуст.");
            return;
        }
        BigDecimal totalAmount = BigDecimal.ZERO;
        responseSender.sendMessage(chatId, "Текущий пул сделок BTC:");
        for (String bot : sortedByBotDeals.keySet()) {
            StringBuilder botDeals = new StringBuilder();
            botDeals.append("\uD83E\uDD16 ").append(bot).append(" ⬇\uFE0F\n");
            for (PoolDeal poolDeal : sortedByBotDeals.get(bot)) {
                botDeals.append("〰\uFE0F〰\uFE0F〰\uFE0F〰\uFE0F〰\uFE0F〰\uFE0F").append("\n")
                        .append("<b>Сделка</b> №<code>").append(poolDeal.getPid()).append("</code> (пул ID ").append(poolDeal.getId())
                        .append(")\n")
                        .append("<b>Данные</b>: <code>").append(poolDeal.getAddress()).append(",")
                        .append(poolDeal.getAmount()).append("</code>").append("\n")
                        .append("<i>Для удаления сделки из пула введите</i> <code>/deletefrompool ")
                        .append(poolDeal.getId()).append("</code>").append("\n");
                totalAmount = totalAmount.add(new BigDecimal(poolDeal.getAmount()));
            }
            responseSender.sendMessage(chatId, botDeals.toString(), "html");
        }
        StringBuilder dealsInfo = new StringBuilder();
        String strTotalAmount = bigDecimalService.roundToPlainString(totalAmount, CryptoCurrency.BITCOIN.getScale());
        dealsInfo.append("<b>Общая сумма</b>: <code>").append(bigDecimalService.roundToPlainString(totalAmount, CryptoCurrency.BITCOIN.getScale())).append("</code>\n");
        dealsInfo.append("<b>Баланс кошелька</b>: <code>").append(cryptoWithdrawalService.getBalance(CryptoCurrency.BITCOIN)).append("</code>");
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
