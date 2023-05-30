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
import tgb.btc.rce.repository.PaymentReceiptRepository;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.service.schedule.DealDeleteScheduler;
import tgb.btc.rce.util.BotImageUtil;
import tgb.btc.rce.util.FiatCurrencyUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.util.List;
import java.util.Objects;

@CommandProcessor(command = Command.NONE)
public class SellBitcoin extends Processor {

    private DealService dealService;
    private PaymentReceiptRepository paymentReceiptRepository;

    private DealRepository dealRepository;

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    private ExchangeService exchangeService;

    private KeyboardService keyboardService;

    private PaymentTypeRepository paymentTypeRepository;

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Autowired
    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    @Autowired
    public void setPaymentReceiptRepository(PaymentReceiptRepository paymentReceiptRepository) {
        this.paymentReceiptRepository = paymentReceiptRepository;
    }

    @Autowired
    public void setKeyboardService(KeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    @Autowired
    public void setExchangeServiceNew(ExchangeService exchangeService) {
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
                if (FiatCurrencyUtil.isFew()) {
                    responseSender.sendMessage(chatId, "Выберите валюту.", keyboardService.getFiatCurrencies());
                    userService.updateCommandByChatId(Command.CHOOSING_FIAT_CURRENCY, chatId);
                } else {
                    dealService.delete(dealService.findById(userService.getCurrentDealByChatId(chatId)));
                    userService.updateCurrentDealByChatId(null, chatId);
                    processToMainMenu(chatId);
                }
            } else previousStep(update);
            return;
        }

        Long currentDealPid;
        Deal deal;
        Integer paymentTypesCount;
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                if (exchangeService.alreadyHasDeal(chatId)) return;
                if (update.hasCallbackQuery() && Command.BACK.getText().equals(update.getCallbackQuery().getData())) {
                    processCancel(chatId);
                    return;
                }
                currentDealPid = userService.getCurrentDealByChatId(chatId);
                if (Objects.isNull(currentDealPid))
                    currentDealPid = dealService.createNewDeal(DealType.SELL, chatId).getPid();
                if (Objects.isNull(dealRepository.getFiatCurrencyByPid(currentDealPid))) {
                    if (FiatCurrencyUtil.isFew()) {
                        responseSender.sendMessage(chatId, "Выберите валюту.", keyboardService.getFiatCurrencies());
                        userService.nextStep(chatId, Command.CHOOSING_FIAT_CURRENCY);
                        return;
                    } else {
                        dealRepository.updateFiatCurrencyByPid(currentDealPid, FiatCurrencyUtil.getFirst());
                    }
                }
                exchangeService.askForCryptoCurrency(chatId);
                userService.nextStep(chatId, Command.SELL_BITCOIN);
                break;
            case 1:
                responseSender.deleteMessage(chatId, Integer.parseInt(userService.getBufferVariable(chatId)));
                if (!update.hasCallbackQuery()) {
                    processToMainMenu(chatId);
                    return;
                }
                CryptoCurrency currency = CryptoCurrency.valueOf(update.getCallbackQuery().getData());
                currentDealPid = userService.getCurrentDealByChatId(chatId);
                dealService.updateCryptoCurrencyByPid(currentDealPid, currency);
                exchangeService.askForSum(chatId, dealRepository.getFiatCurrencyByPid(currentDealPid), currency, dealService.getDealTypeByPid(currentDealPid));
                userService.nextStep(chatId);
                break;
            case 2:
                if (UpdateType.INLINE_QUERY.equals(UpdateType.fromUpdate(update))) {
                    exchangeService.calculateForInlineQuery(update);
                    return;
                }
                if (exchangeService.calculateDealAmount(chatId, UpdateUtil.getBigDecimalFromText(update))) {
                    currentDealPid = userService.getCurrentDealByChatId(chatId);
                    DealType dealType = dealRepository.getDealTypeByPid(currentDealPid);
                    FiatCurrency fiatCurrency = dealRepository.getFiatCurrencyByPid(currentDealPid);
                    paymentTypesCount = paymentTypeRepository.countByDealTypeAndIsOnAndFiatCurrency(dealType, true, fiatCurrency);
                    if (Objects.isNull(paymentTypesCount) || paymentTypesCount == 0)
                        throw new BaseException("Не найден ни один тип оплаты.");
                    if (paymentTypesCount > 1) {
                        exchangeService.askForPaymentType(update);
                        userService.nextStep(chatId);
                        responseSender.deleteMessage(UpdateUtil.getChatId(update), UpdateUtil.getMessage(update).getMessageId());
                    } else {
                        userService.updateBufferVariable(chatId,
                                paymentTypeRepository.getByDealTypeAndIsOnAndFiatCurrency(dealType, true, fiatCurrency).get(0).getPid().toString());
                        userService.nextStep(chatId);
                        run(update);
                    }
                }
                break;
            case 3:
                currentDealPid = userService.getCurrentDealByChatId(chatId);
                paymentTypesCount = paymentTypeRepository.countByDealTypeAndIsOnAndFiatCurrency(dealRepository.getDealTypeByPid(currentDealPid),
                        true, dealRepository.getFiatCurrencyByPid(currentDealPid));
                Boolean result;
                if (paymentTypesCount > 1) {
                    result = exchangeService.savePaymentType(update);
                } else {
                    dealService.updatePaymentTypeByPid(paymentTypeRepository.getByPid(Long.parseLong(userService.getBufferVariable(chatId))), currentDealPid);
                    result = true;
                }
                if (BooleanUtils.isTrue(result)) {
                    exchangeService.askForUserRequisites(update);
                    userService.nextStep(chatId);
                } else if (BooleanUtils.isFalse(result)) responseSender.sendMessage(chatId, "Выберите способ оплаты.");
                break;
            case 4:
                if (update.hasCallbackQuery()) responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                exchangeService.saveRequisites(update);
                exchangeService.buildDeal(update);
                userService.nextStep(chatId);
                break;
            case 5:
                Long dealPid = userService.getCurrentDealByChatId(chatId);
                if (update.hasCallbackQuery() && Command.CANCEL_DEAL.name().equals(update.getCallbackQuery().getData())) {
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
                    DealDeleteScheduler.deleteCryptoDeal(dealPid);
                    break;
                }
                break;
            case 6:
                if (update.hasMessage() && Command.RECEIPTS_CANCEL_DEAL.getText().equals(UpdateUtil.getMessageText(update))) {
                    dealPid = userService.getCurrentDealByChatId(chatId);
                    DealDeleteScheduler.deleteCryptoDeal(dealPid);
                    if (update.hasCallbackQuery()) responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                    dealService.delete(dealService.findById(dealPid));
                    userService.updateCurrentDealByChatId(null, chatId);
                    responseSender.sendMessage(chatId, "Заявка отменена.");
                    processToMainMenu(chatId);
                }
                if (!update.hasMessage() || (!update.getMessage().hasPhoto() && !update.getMessage().hasDocument())) {
                    responseSender.sendMessage(chatId, "Отправьте скрин перевода, либо чек оплаты..");
                    return;
                }
                if (update.hasMessage() && update.getMessage().hasPhoto()) {
                    deal = dealService.getByPid(userService.getCurrentDealByChatId(chatId));
                    PaymentReceipt paymentReceipt = paymentReceiptRepository.save(PaymentReceipt.builder()
                            .receipt(BotImageUtil.getImageId(update.getMessage().getPhoto()))
                            .receiptFormat(ReceiptFormat.PICTURE)
                            .build());
                    List<PaymentReceipt> paymentReceipts = dealService.getPaymentReceipts(deal.getPid());
                    paymentReceipts.add(paymentReceipt);
                    deal.setPaymentReceipts(paymentReceipts);
                    dealService.save(deal);
                    DealDeleteScheduler.deleteCryptoDeal(deal.getPid());
                } else if (update.hasMessage() && update.getMessage().hasDocument()) {
                    deal = dealService.getByPid(userService.getCurrentDealByChatId(chatId));
                    PaymentReceipt paymentReceipt = paymentReceiptRepository.save(PaymentReceipt.builder()
                            .receipt(update.getMessage().getDocument().getFileId())
                            .receiptFormat(ReceiptFormat.PDF)
                            .build());
                    List<PaymentReceipt> paymentReceipts = dealService.getPaymentReceipts(deal.getPid());
                    paymentReceipts.add(paymentReceipt);
                    deal.setPaymentReceipts(paymentReceipts);
                    dealService.save(deal);
                }
                exchangeService.confirmDeal(update);
                processToMainMenu(chatId);
                break;
        }
    }

    private void previousStep(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        userService.previousStep(chatId);

        Integer paymentTypesCount;
        switch (userService.getStepByChatId(chatId)) {
            case 1:
                exchangeService.askForCryptoCurrency(chatId);
                break;
            case 2:
                Long currentDealPid = userService.getCurrentDealByChatId(chatId);
                dealRepository.updateIsPersonalAppliedByPid(currentDealPid, false);
                exchangeService.askForSum(chatId, dealRepository.getFiatCurrencyByPid(currentDealPid),
                                          dealService.getCryptoCurrencyByPid(currentDealPid), dealService.getDealTypeByPid(currentDealPid));
                break;
            case 3:
                currentDealPid = userService.getCurrentDealByChatId(chatId);
                paymentTypesCount = paymentTypeRepository.countByDealTypeAndIsOnAndFiatCurrency(dealRepository.getDealTypeByPid(currentDealPid),
                        true, dealRepository.getFiatCurrencyByPid(currentDealPid));
                if (paymentTypesCount > 1) {
                    exchangeService.askForPaymentType(update);
                } else {
                    userService.previousStep(chatId);
                    previousStep(update);
                }
                break;
            case 4:
                exchangeService.askForUserRequisites(update);
                break;
        }
    }
}
