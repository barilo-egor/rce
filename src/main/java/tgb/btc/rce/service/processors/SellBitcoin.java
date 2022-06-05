package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.UpdateType;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.service.processors.support.SellService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.SELL_BITCOIN)
public class SellBitcoin extends Processor {
    
    private final DealService dealService;
    private final SellService sellService;

    @Autowired
    public SellBitcoin(IResponseSender responseSender, UserService userService, DealService dealService, SellService sellService) {
        super(responseSender, userService);
        this.dealService = dealService;
        this.sellService = sellService;
    }

    @Override
    public void run(Update update) {
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
                sellService.createDeal(chatId);
                sellService.askForCurrency(chatId);
                userService.nextStep(chatId, Command.SELL_BITCOIN);
                break;
            case 1:
                responseSender.deleteMessage(chatId, Integer.parseInt(userService.getBufferVariable(chatId)));
                if (!update.hasCallbackQuery()) {
                    processToMainMenu(chatId);
                    return;
                }
                CryptoCurrency currency = CryptoCurrency.valueOf(update.getCallbackQuery().getData());
                dealService.updateCryptoCurrencyByPid(userService.getCurrentDealByChatId(chatId), currency);
                sellService.askForSum(chatId, currency);
                userService.nextStep(chatId);
                break;
            case 2:
                if (UpdateType.INLINE_QUERY.equals(UpdateType.fromUpdate(update))) {
                    sellService.convertToRub(update,
                            userService.getCurrentDealByChatId(UpdateUtil.getChatId(update)));
                    return;
                }
                sellService.saveSum(update);
                sellService.askForPaymentType(update);
                userService.nextStep(chatId);
                responseSender.deleteMessage(UpdateUtil.getChatId(update), Integer.parseInt(userService.getBufferVariable(chatId)));
                break;
            case 3:
                sellService.savePaymentType(update);
                responseSender.deleteMessage(UpdateUtil.getChatId(update), UpdateUtil.getMessage(update).getMessageId());
                sellService.askForWallet(update);
                userService.nextStep(chatId);
                break;
            case 4:
                sellService.saveWallet(update);
                sellService.buildDeal(update);
                userService.nextStep(chatId);
                responseSender.deleteMessage(UpdateUtil.getChatId(update), UpdateUtil.getMessage(update).getMessageId());
                responseSender.deleteMessage(UpdateUtil.getChatId(update), Integer.parseInt(userService.getBufferVariable(chatId)));
                break;
            case 5:
                if (update.hasCallbackQuery() && Command.CANCEL_DEAL.name().equals(update.getCallbackQuery().getData())) {
                    responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                    dealService.delete(dealService.findById(userService.getCurrentDealByChatId(chatId)));
                    userService.updateCurrentDealByChatId(null, chatId);
                    responseSender.sendMessage(chatId, "Заявка отменена.");
                    processToMainMenu(chatId);
                    return;
                } else if (update.hasCallbackQuery() && Command.PAID.name().equals(update.getCallbackQuery().getData())) {
                    responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                    sellService.confirmDeal(update);
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
                sellService.askForCurrency(chatId);
                break;
            case 2:
                sellService.askForSum(chatId,
                        dealService.getCryptoCurrencyByPid(userService.getCurrentDealByChatId(chatId)));
                break;
            case 3:
                sellService.askForPaymentType(update);
                break;
            case 4:
                sellService.askForWallet(update);
                break;
        }
    }
}
