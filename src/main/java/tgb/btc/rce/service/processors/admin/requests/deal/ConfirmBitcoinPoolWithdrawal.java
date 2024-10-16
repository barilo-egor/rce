package tgb.btc.rce.service.processors.admin.requests.deal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.process.IDealPoolService;
import tgb.btc.library.service.AutoWithdrawalService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@CommandProcessor(command = Command.CONFIRM_POOL_WITHDRAWAL)
@Slf4j
public class ConfirmBitcoinPoolWithdrawal extends Processor {

    private final IDealPoolService dealPoolService;

    private final AutoWithdrawalService autoWithdrawalService;

    private final IModifyDealService modifyDealService;

    @Autowired
    public ConfirmBitcoinPoolWithdrawal(IDealPoolService dealPoolService, AutoWithdrawalService autoWithdrawalService,
                                        IModifyDealService modifyDealService) {
        this.dealPoolService = dealPoolService;
        this.autoWithdrawalService = autoWithdrawalService;
        this.modifyDealService = modifyDealService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        Integer messageId = updateService.getMessageId(update);
        log.debug("Пользователь chatId={} подтвердил вывод сделок из пула.", chatId);
        try {
            synchronized (dealPoolService) {
                List<Deal> deals = dealPoolService.getAllByDealStatusAndCryptoCurrency(CryptoCurrency.BITCOIN);
                BigDecimal totalAmount = deals.stream()
                        .map(Deal::getCryptoAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal balance = autoWithdrawalService.getBalance(CryptoCurrency.BITCOIN);
                if (balance.compareTo(totalAmount) < 0) {
                    responseSender.sendAnswerCallbackQuery(update.getCallbackQuery().getId(),
                            "На балансе недостаточно средств для автовывода. Текущий баланс: " + balance.toPlainString(), true);
                    return;
                }
                autoWithdrawalService.withdrawal(deals.stream().map(Deal::getPid).collect(Collectors.toList()));
                deals.forEach(deal -> modifyDealService.confirm(deal.getPid()));
                dealPoolService.completePool(CryptoCurrency.BITCOIN, chatId);
            }
        } catch (Exception e) {
            responseSender.sendMessage(chatId, "Ошибка:" + e.getMessage());
            return;
        }
        responseSender.deleteMessage(chatId, messageId);
        responseSender.deleteCallbackMessageIfExists(update);
        responseSender.sendMessage(chatId, "Сделки из пула успешно выведены. Пул очищен.");
    }
}
