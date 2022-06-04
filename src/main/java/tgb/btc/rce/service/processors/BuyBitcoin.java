package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.UpdateType;
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
        if(update.hasCallbackQuery() && Command.BACK.getText().equals(update.getCallbackQuery().getData())) {
            responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
            if (userService.getStepByChatId(chatId) == 1) {
                dealService.delete(dealService.findById(userService.getCurrentDealByChatId(chatId)));
                userService.updateCurrentDealByChatId(null, chatId);
                processToMainMenu(chatId);
            }
            else previousStep(update);
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
                CryptoCurrency currency = CryptoCurrency.valueOf(update.getCallbackQuery().getData());
                dealService.updateCryptoCurrencyByPid(userService.getCurrentDealByChatId(chatId), currency);
                exchangeService.askForSum(chatId, currency);
                break;
            case 2:
                if(UpdateType.INLINE_QUERY.equals(UpdateType.fromUpdate(update))) {
                    exchangeService.convertToRub(update,
                            userService.getCurrentDealByChatId(UpdateUtil.getChatId(update)));
                    return;
                }
                exchangeService.saveSum(update);
                if (dealService.getDealsCountByUserChatId(chatId) > 1) exchangeService.askForWallet(chatId);
                else exchangeService.askForUserPromoCode(chatId);
                break;
            case 3:
                exchangeService.processPromoCode(update);
                exchangeService.askForWallet(chatId);
                break;
            case 4:
                exchangeService.saveWallet(update);
                exchangeService.askForPaymentType(update);
                break;
            case 5:
                exchangeService.savePaymentType(update);
                break;
        }
    }

    private void previousStep(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        userService.previousStep(chatId);

        switch (userService.getStepByChatId(chatId)) {
            case 1:
                exchangeService.askForCurrency(chatId);
                break;
            case 2:
                exchangeService.askForSum(chatId,
                        dealService.getCryptoCurrencyByPid(userService.getCurrentDealByChatId(chatId)));
                break;
            case 3:
                if (dealService.getDealsCountByUserChatId(chatId) > 0) exchangeService.askForWallet(chatId);
                else exchangeService.askForUserPromoCode(chatId);
                break;
            case 4:
                if (dealService.getDealsCountByUserChatId(chatId) > 1) exchangeService.askForWallet(chatId);
                else exchangeService.askForUserPromoCode(chatId);
        }
        userService.previousStep(chatId);
    }
}
