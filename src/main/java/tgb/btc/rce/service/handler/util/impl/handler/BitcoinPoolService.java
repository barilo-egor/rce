package tgb.btc.rce.service.handler.util.impl.handler;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DeliveryType;
import tgb.btc.library.exception.ApiResponseErrorException;
import tgb.btc.library.interfaces.util.IBigDecimalService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.library.vo.web.PoolDeal;
import tgb.btc.rce.enums.HTMLTag;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.util.IBitcoinPoolService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class BitcoinPoolService implements IBitcoinPoolService {

    private final IResponseSender responseSender;

    private final ICryptoWithdrawalService cryptoWithdrawalService;

    private final IBigDecimalService bigDecimalService;

    private final IKeyboardBuildService keyboardBuildService;

    private final ICallbackDataService callbackDataService;

    public BitcoinPoolService(IResponseSender responseSender, ICryptoWithdrawalService cryptoWithdrawalService,
                              IBigDecimalService bigDecimalService,
                              IKeyboardBuildService keyboardBuildService, ICallbackDataService callbackDataService) {
        this.responseSender = responseSender;
        this.cryptoWithdrawalService = cryptoWithdrawalService;
        this.bigDecimalService = bigDecimalService;
        this.keyboardBuildService = keyboardBuildService;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void process(Long chatId) {
        List<PoolDeal> deals;
        Optional<Message> poolMessage = responseSender.sendMessage(chatId, "Получение пула сделок BTC.");
        try {
            deals = cryptoWithdrawalService.getAllPoolDeals();
            Map<String, List<PoolDeal>> sortedByBotDeals = deals.stream()
                    .collect(Collectors.groupingBy(PoolDeal::getBot, TreeMap::new, Collectors.toList()));
            if (CollectionUtils.isEmpty(deals)) {
                responseSender.sendMessage(chatId, "Текущий пул сделок BTC пуст.");
                return;
            }
            BigDecimal totalAmount = BigDecimal.ZERO;
            responseSender.sendMessage(chatId, "Текущий пул сделок BTC:");
            for (Map.Entry<String, List<PoolDeal>> bot : sortedByBotDeals.entrySet()) {
                StringBuilder botDeals = new StringBuilder();
                botDeals.append("\uD83E\uDD16 ").append(bot.getKey()).append(" ⬇️\n");
                for (PoolDeal poolDeal : bot.getValue()) {
                    botDeals.append("〰️〰️〰️〰️〰️〰️").append("\n")
                            .append(HTMLTag.BOLD.getOpenTag()).append("Сделка").append(HTMLTag.BOLD.getCloseTag()).append(" №").append(HTMLTag.CODE.getOpenTag()).append(poolDeal.getPid())
                            .append(HTMLTag.CODE.getCloseTag()).append(" (пул ID ").append(poolDeal.getId())
                            .append(") ").append(poolDeal.getAddDate().format(DateTimeFormatter.ofPattern("HH:mm")))
                            .append(DeliveryType.VIP.equals(poolDeal.getDeliveryType()) ? " <b>VIP</b>\n" : "\n")
                            .append("<b>Данные</b>: ").append(HTMLTag.CODE.getOpenTag()).append(poolDeal.getAddress()).append(",")
                            .append(poolDeal.getAmount()).append(HTMLTag.CODE.getCloseTag()).append("\n")
                            .append("<i>Для удаления сделки из пула введите</i> ").append(HTMLTag.CODE.getOpenTag())
                            .append("/deletefrompool ")
                            .append(poolDeal.getId()).append(HTMLTag.CODE.getCloseTag()).append("\n");
                    totalAmount = totalAmount.add(new BigDecimal(poolDeal.getAmount()));
                }
                responseSender.sendMessage(chatId, botDeals.toString());
            }
            StringBuilder dealsInfo = new StringBuilder();
            String strTotalAmount = bigDecimalService.roundToPlainString(totalAmount, CryptoCurrency.BITCOIN.getScale());
            dealsInfo.append("<b>Общая сумма</b>: ").append(HTMLTag.CODE.getOpenTag())
                    .append(bigDecimalService.roundToPlainString(totalAmount, CryptoCurrency.BITCOIN.getScale()))
                    .append(HTMLTag.CODE.getCloseTag()).append("\n");
            dealsInfo.append("<b>Баланс кошелька</b>: ").append(HTMLTag.CODE.getOpenTag())
                    .append(cryptoWithdrawalService.getBalance(CryptoCurrency.BITCOIN))
                    .append(HTMLTag.CODE.getCloseTag());
            ReplyKeyboard replyKeyboard = keyboardBuildService.buildInline(
                    List.of(
                            InlineButton.builder()
                                    .text("Автовывод")
                                    .data(callbackDataService.buildData(CallbackQueryData.BITCOIN_POOL_WITHDRAWAL, deals.size(), strTotalAmount, false))
                                    .build(),
                            InlineButton.builder()
                                    .text("Автовывод(ком.)")
                                    .data(callbackDataService.buildData(CallbackQueryData.BITCOIN_POOL_WITHDRAWAL, deals.size(), strTotalAmount, true))
                                    .build(),
                            InlineButton.builder()
                                    .text("Очистить пул")
                                    .data(callbackDataService.buildData(CallbackQueryData.CLEAR_POOL, deals.size()))
                                    .build(),
                            InlineButton.builder()
                                    .text("❌ Закрыть")
                                    .data(CallbackQueryData.INLINE_DELETE.name())
                                    .build()
                    ), 2);
            responseSender.sendMessage(chatId, dealsInfo.toString(), replyKeyboard);
        } catch (ApiResponseErrorException e) {
            responseSender.sendMessage(chatId, e.getMessage());
        } finally {
            poolMessage.ifPresent(message -> responseSender.deleteMessage(chatId, message.getMessageId()));
        }
    }
}
