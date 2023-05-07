package tgb.btc.rce.service.processors;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.PaymentReceipt;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.exception.NumberParseException;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.PaymentReceiptsService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.service.processors.support.ExchangeServiceNew;
import tgb.btc.rce.service.processors.support.SellService;
import tgb.btc.rce.service.schedule.DealDeleteScheduler;
import tgb.btc.rce.util.BotImageUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.SELL_BITCOIN)
public class SellBitcoin extends Processor {
    
    private final DealService dealService;
    private final SellService sellService;
    private final PaymentReceiptsService paymentReceiptsService;
    private final ExchangeService exchangeService;

    private DealRepository dealRepository;

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    private ExchangeServiceNew exchangeServiceNew;

    @Autowired
    public void setExchangeServiceNew(ExchangeServiceNew exchangeServiceNew) {
        this.exchangeServiceNew = exchangeServiceNew;
    }

    public SellBitcoin(IResponseSender responseSender, UserService userService, DealService dealService,
                       SellService sellService, PaymentReceiptsService paymentReceiptsService,
                       ExchangeService exchangeService) {
        super(responseSender, userService);
        this.dealService = dealService;
        this.sellService = sellService;
        this.paymentReceiptsService = paymentReceiptsService;
        this.exchangeService = exchangeService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        try {
            if (isMainMenuCommand(update)) processCancel(chatId);
            else processSelling(update);
        } catch (NumberParseException e) {
            processCancel(chatId);
        } catch (BaseException e) {
            responseSender.sendMessage(chatId, e.getMessage());
            processCancel(chatId);
        }
    }

    public void processCancel(Long chatId) {
        dealService.delete(dealService.findById(userService.getCurrentDealByChatId(chatId)));
        userService.updateCurrentDealByChatId(null, chatId);
        processToMainMenu(chatId);
    }

    private boolean isMainMenuCommand(Update update) {
        try {
            return userService.getStepByChatId(UpdateUtil.getChatId(update)) != User.DEFAULT_STEP && update.hasMessage() && update.getMessage().hasText()
                    && (Command.BUY_BITCOIN.equals(Command.fromUpdate(update))
                    || Command.SELL_BITCOIN.equals(Command.fromUpdate(update))
                    || Command.CONTACTS.equals(Command.fromUpdate(update))
                    || Command.DRAWS.equals(Command.fromUpdate(update))
                    || Command.REFERRAL.equals(Command.fromUpdate(update))
                    || Command.ADMIN_PANEL.equals(Command.fromUpdate(update)));
        } catch (BaseException e) {
            return false;
        }
    }

    public void processSelling(Update update) {
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
                dealService.createNewDeal(DealType.SELL, chatId);
                exchangeServiceNew.askForCurrency(chatId, DealType.SELL);
                userService.nextStep(chatId, Command.SELL_BITCOIN);
                break;
            case 1:
                responseSender.deleteMessage(chatId, Integer.parseInt(userService.getBufferVariable(chatId)));
                if (!update.hasCallbackQuery()) {
                    processToMainMenu(chatId);
                    return;
                }
                CryptoCurrency currency = CryptoCurrency.valueOf(update.getCallbackQuery().getData());
                Long currentDealPid = userService.getCurrentDealByChatId(chatId);
                dealService.updateCryptoCurrencyByPid(currentDealPid, currency);
                exchangeServiceNew.askForSum(chatId, currency, dealService.getDealTypeByPid(currentDealPid));
                userService.nextStep(chatId);
                break;
            case 2:
                if (UpdateType.INLINE_QUERY.equals(UpdateType.fromUpdate(update))) {
                    sellService.convertToRub(update,
                            userService.getCurrentDealByChatId(UpdateUtil.getChatId(update)));
                    return;
                }
                if (sellService.saveSum(update)) {
                    sellService.askForPaymentType(update);
                    userService.nextStep(chatId);
                    responseSender.deleteMessage(UpdateUtil.getChatId(update), Integer.parseInt(userService.getBufferVariable(chatId)));
                }
                break;
            case 3:
                Boolean result = sellService.savePaymentType(update);
                if (BooleanUtils.isTrue(result)) {
                    sellService.askForWallet(update);
                    userService.nextStep(chatId);
                } else if (BooleanUtils.isFalse(result)) responseSender.sendMessage(chatId, "Выберите способ оплаты.");
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
                    Long dealPid = userService.getCurrentDealByChatId(chatId);
                    DealDeleteScheduler.deleteCryptoDeal(dealPid);
                    responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                    dealService.delete(dealService.findById(dealPid));
                    userService.updateCurrentDealByChatId(null, chatId);
                    responseSender.sendMessage(chatId, "Заявка отменена.");
                    processToMainMenu(chatId);
                    return;
                } else if (update.hasCallbackQuery() && Command.PAID.name().equals(update.getCallbackQuery().getData())) {
                    responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                    exchangeService.askForReceipts(update);
                    userService.nextStep(chatId);
                    break;
                }
                break;
            case 6:
                if (update.hasMessage() && update.getMessage().hasPhoto()) {
                    Deal deal = dealService.getByPid(userService.getCurrentDealByChatId(chatId));
                    PaymentReceipt paymentReceipt = paymentReceiptsService.save(PaymentReceipt.builder()
                            .receipt(BotImageUtil.getImageId(update.getMessage().getPhoto()))
                            .receiptFormat(ReceiptFormat.PICTURE)
                            .build());
                    List<PaymentReceipt> paymentReceipts = dealService.getPaymentReceipts(deal.getPid());
                    paymentReceipts.add(paymentReceipt);
                    deal.setPaymentReceipts(paymentReceipts);
                    dealService.save(deal);
                } else if (update.hasMessage() && update.getMessage().hasDocument()) {
                    Deal deal = dealService.getByPid(userService.getCurrentDealByChatId(chatId));
                    PaymentReceipt paymentReceipt = paymentReceiptsService.save(PaymentReceipt.builder()
                            .receipt(update.getMessage().getDocument().getFileId())
                            .receiptFormat(ReceiptFormat.PDF)
                            .build());
                    List<PaymentReceipt> paymentReceipts = dealService.getPaymentReceipts(deal.getPid());
                    paymentReceipts.add(paymentReceipt);
                    deal.setPaymentReceipts(paymentReceipts);
                    dealService.save(deal);
                }
                sellService.confirmDeal(update);
                processToMainMenu(chatId);
                break;
        }
    }

    private void previousStep(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        userService.previousStep(chatId);

        switch (userService.getStepByChatId(chatId)) {
            case 1:
                exchangeServiceNew.askForCurrency(chatId, DealType.SELL);
                break;
            case 2:
                Long currentDealPid = userService.getCurrentDealByChatId(chatId);
                dealRepository.uppateIsPersonalAppliedByPid(currentDealPid, false);
                exchangeServiceNew.askForSum(chatId,
                        dealService.getCryptoCurrencyByPid(currentDealPid), dealService.getDealTypeByPid(currentDealPid));
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
