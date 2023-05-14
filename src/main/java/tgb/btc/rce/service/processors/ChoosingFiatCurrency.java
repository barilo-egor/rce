package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.CHOOSING_FIAT_CURRENCY, step = 1)
public class ChoosingFiatCurrency extends Processor {

    private DealRepository dealRepository;

    private BuyBitcoin buyBitcoin;

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Autowired
    public void setBuyBitcoin(BuyBitcoin buyBitcoin) {
        this.buyBitcoin = buyBitcoin;
    }

    @Autowired
    public ChoosingFiatCurrency(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (!UpdateUtil.hasMessageText(update)) {
            responseSender.sendMessage(chatId, "Выберите валюту.");
            return;
        }
        String enteredCurrency = UpdateUtil.getMessageText(update);
        FiatCurrency fiatCurrency = FiatCurrency.valueOf(enteredCurrency);
        dealRepository.updateFiatCurrencyByPid(userService.getCurrentDealByChatId(chatId), fiatCurrency);
        buyBitcoin.run(update);
    }
}
