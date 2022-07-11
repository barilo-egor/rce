package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.UpdateType;
import tgb.btc.rce.exception.NumberParseException;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

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
        try {
            process(update);
        } catch (NumberParseException e) {
            dealService.delete(dealService.findById(userService.getCurrentDealByChatId(chatId)));
            userService.updateCurrentDealByChatId(null, chatId);
            processToMainMenu(chatId);
        }
    }

    private void process(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) {
            dealService.delete(dealService.findById(userService.getCurrentDealByChatId(chatId)));
            userService.updateCurrentDealByChatId(null, chatId);
            return;
        }
        if (update.hasCallbackQuery() && Command.BACK.getText().equals(update.getCallbackQuery().getData())) {
            responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
            if (userService.getStepByChatId(chatId) == 1) {
                dealService.delete(dealService.findById(userService.getCurrentDealByChatId(chatId)));
                userService.updateCurrentDealByChatId(null, chatId);
                processToMainMenu(chatId);
            } else previousStep(update);
            return;
        }

        switch (userService.getStepByChatId(chatId)) {
            case 0:
                if (dealService.getActiveDealsCountByUserChatId(chatId) > 0) {
                    responseSender.sendMessage(chatId, "У вас уже есть активная заявка.",
                            KeyboardUtil.buildInline(List.of(InlineButton.builder()
                                    .inlineType(InlineType.CALLBACK_DATA)
                                    .text("Удалить")
                                    .data(Command.DELETE_DEAL.getText())
                                    .build())));
                    return;
                }
                exchangeService.createDeal(chatId);
                exchangeService.askForCurrency(chatId);
                userService.nextStep(chatId, Command.BUY_BITCOIN);
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
                userService.nextStep(chatId);
                break;
            case 2:
                if (UpdateType.INLINE_QUERY.equals(UpdateType.fromUpdate(update))) {
                    exchangeService.convertToRub(update,
                            userService.getCurrentDealByChatId(UpdateUtil.getChatId(update)));
                    return;
                }
                if (!exchangeService.saveSum(update)) return;
                responseSender.deleteMessage(UpdateUtil.getChatId(update), Integer.parseInt(userService.getBufferVariable(chatId)));
                if (dealService.getDealsCountByUserChatId(chatId) < 2) {
                    exchangeService.askForUserPromoCode(chatId);
                } else if (userService.getReferralBalanceByChatId(chatId) > 0) {
                    exchangeService.askForReferralDiscount(update);
                } else {
                    exchangeService.askForWallet(update);
                    userService.nextStep(chatId);
                }
                userService.nextStep(chatId);
                break;
            case 3:
                if (dealService.getDealsCountByUserChatId(chatId) < 2) {
                    exchangeService.processPromoCode(update);
                } else if (update.hasCallbackQuery()
                        && update.getCallbackQuery().getData().equals(ExchangeService.USE_REFERRAL_DISCOUNT)){
                    exchangeService.processReferralDiscount(update);
                }
                userService.nextStep(chatId);
                exchangeService.askForWallet(update);
                responseSender.deleteMessage(UpdateUtil.getChatId(update), UpdateUtil.getMessage(update).getMessageId());
                break;
            case 4:
                exchangeService.saveWallet(update);
                exchangeService.askForPaymentType(update);
                userService.nextStep(chatId);
                responseSender.deleteMessage(UpdateUtil.getChatId(update), UpdateUtil.getMessage(update).getMessageId());
                responseSender.deleteMessage(UpdateUtil.getChatId(update), Integer.parseInt(userService.getBufferVariable(chatId)));
                break;
            case 5:
                exchangeService.savePaymentType(update);
                exchangeService.buildDeal(update);
                userService.nextStep(chatId);
                break;
            case 6:
                if (update.hasCallbackQuery() && Command.CANCEL_DEAL.name().equals(update.getCallbackQuery().getData())) {
                    responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                    dealService.delete(dealService.findById(userService.getCurrentDealByChatId(chatId)));
                    userService.updateCurrentDealByChatId(null, chatId);
                    responseSender.sendMessage(chatId, "Заявка отменена.");
                    processToMainMenu(chatId);
                    return;
                } else if (update.hasCallbackQuery() && Command.PAID.name().equals(update.getCallbackQuery().getData())) {
                    responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                    exchangeService.confirmDeal(update);
                    break;
                }
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
                if (dealService.getDealsCountByUserChatId(chatId) < 1) {
                    exchangeService.askForUserPromoCode(chatId);
                } else if (userService.getReferralBalanceByChatId(chatId) > 0) {
                    exchangeService.askForReferralDiscount(update);
                } else {
                    exchangeService.askForSum(chatId,
                            dealService.getCryptoCurrencyByPid(userService.getCurrentDealByChatId(chatId)));
                    userService.previousStep(chatId);
                }
                break;
            case 4:
                exchangeService.askForWallet(update);
                break;
            case 5:
                exchangeService.askForPaymentType(update);
                break;
        }
    }
}
