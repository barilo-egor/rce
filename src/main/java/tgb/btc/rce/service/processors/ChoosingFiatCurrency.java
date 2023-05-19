package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.CHOOSING_FIAT_CURRENCY, step = 1)
public class ChoosingFiatCurrency extends Processor {

    private DealRepository dealRepository;

    private BuyBitcoin buyBitcoin;

    private SellBitcoin sellBitcoin;

    @Autowired
    public void setSellBitcoin(SellBitcoin sellBitcoin) {
        this.sellBitcoin = sellBitcoin;
    }

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Autowired
    public void setBuyBitcoin(BuyBitcoin buyBitcoin) {
        this.buyBitcoin = buyBitcoin;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (buyBitcoin.isMainMenuCommand(update)) {
            buyBitcoin.processCancel(chatId);
            return;
        }
        if (!update.hasCallbackQuery()) {
            responseSender.sendMessage(chatId, "Выберите валюту.");
            return;
        }
        Long currentDealPid = userService.getCurrentDealByChatId(chatId);
        boolean isBuy = DealType.isBuy(dealRepository.getDealTypeByPid(currentDealPid));
        if (Command.BACK.getText().equals(update.getCallbackQuery().getData())) {
            if (isBuy) buyBitcoin.processCancel(chatId);
            else sellBitcoin.processCancel(chatId);
            responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
            return;
        }
        String enteredCurrency = CallbackQueryUtil.getSplitData(update, 1);
        FiatCurrency fiatCurrency = FiatCurrency.valueOf(enteredCurrency);
        dealRepository.updateFiatCurrencyByPid(currentDealPid, fiatCurrency);
        userRepository.updateStepAndCommandByChatId(chatId, Command.BUY_BITCOIN, User.DEFAULT_STEP);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        if (isBuy) buyBitcoin.run(update);
        else sellBitcoin.run(update);
    }
}
