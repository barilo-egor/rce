package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.BUY_BITCOIN)
public class BuyBitcoin extends Processor {

    private final ExchangeService exchangeService;
    private final DealService dealService;

    @Autowired
    public BuyBitcoin(IResponseSender responseSender, UserService userService, ExchangeService exchangeService,
                      DealService dealService) {
        super(responseSender, userService);
        this.exchangeService = exchangeService;
        this.dealService = dealService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) {
            dealService.delete(dealService.findById(userService.getCurrentDealByChatId(chatId)));
            userService.updateCurrentDealByChatId(null, chatId);
            return;
        }
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                exchangeService.createDeal(chatId);
                exchangeService.askForCurrency(chatId);
                break;
            case 1:
                responseSender.deleteMessage(chatId, Integer.parseInt(userService.getBufferVariable(chatId)));
                if (!update.hasCallbackQuery()) {
                    processToMainMenu(chatId);
                    return;
                }
                dealService.updateCryptoCurrencyByPid(userService.getCurrentDealByChatId(chatId),
                        CryptoCurrency.valueOf(update.getCallbackQuery().getData()));
                exchangeService.askForSum(chatId);
                break;
            case 2:
                exchangeService.validateSum(update);
                break;
        }
    }
}
