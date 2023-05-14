package tgb.btc.rce.service.processors;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.PaymentReceipt;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.exception.NumberParseException;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.repository.PaymentReceiptRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.*;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.service.processors.support.ExchangeServiceNew;
import tgb.btc.rce.service.schedule.DealDeleteScheduler;
import tgb.btc.rce.util.*;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CommandProcessor(command = Command.BUY_BITCOIN)
public class BuyBitcoin extends Processor {

    private final static DealType DEAL_TYPE = DealType.BUY;

    private final ExchangeService exchangeService;

    private final DealService dealService;

    private final PaymentReceiptRepository paymentReceiptRepository;

    private ExchangeServiceNew exchangeServiceNew;

    private KeyboardService keyboardService;

    private MessageService messageService;

    private DealRepository dealRepository;

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Autowired
    public void setKeyboardService(KeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setExchangeServiceNew(ExchangeServiceNew exchangeServiceNew) {
        this.exchangeServiceNew = exchangeServiceNew;
    }

    private static final List<Command> MAIN_MENU_COMMANDS = Arrays.asList(Command.BUY_BITCOIN, Command.SELL_BITCOIN,
            Command.CONTACTS, Command.DRAWS, Command.REFERRAL, Command.ADMIN_PANEL);

    @Autowired
    public BuyBitcoin(IResponseSender responseSender, UserService userService, ExchangeService exchangeService,
                      DealService dealService, PaymentReceiptRepository paymentReceiptRepository) {
        super(responseSender, userService);
        this.exchangeService = exchangeService;
        this.dealService = dealService;
        this.paymentReceiptRepository = paymentReceiptRepository;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        try {
            if (isMainMenuCommand(update)) processCancel(chatId);
            else processBuying(update);
        } catch (NumberParseException e) {
            processCancel(chatId);
        } catch (BaseException e) {
            responseSender.sendMessage(chatId, e.getMessage());
            processCancel(chatId);
        }
    }

    private boolean isMainMenuCommand(Update update) {
        try {
            return !userService.isDefaultStep(UpdateUtil.getChatId(update)) && UpdateUtil.hasMessageText(update)
                    && MAIN_MENU_COMMANDS.stream().anyMatch(command -> command.equals(Command.fromUpdate(update)));
        } catch (BaseException e) {
            return false;
        }
    }

    public void processCancel(Long chatId) {
        Long currentDealPid = userService.getCurrentDealByChatId(chatId);
        if (Objects.nonNull(currentDealPid)) {
            dealService.deleteById(currentDealPid);
            userService.updateCurrentDealByChatId(null, chatId);
        }
        processToMainMenu(chatId);
    }

    private void processBuying(Update update) {
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
        Deal deal;
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                if (dealService.getActiveDealsCountByUserChatId(chatId) > 0) {
                    responseSender.sendMessage(chatId, "У вас уже есть активная заявка.",
                            InlineButton.buildData("Удалить", Command.DELETE_DEAL.getText()));
                    return;
                }
                deal = dealService.createNewDeal(DEAL_TYPE, chatId);
                if (Objects.isNull(dealRepository.getFiatCurrencyByPid(userService.getCurrentDealByChatId(chatId)))
                        && FiatCurrenciesUtil.isFew()) {
                    responseSender.sendMessage(chatId, "Выберите валюту.", keyboardService.getFiatCurrencies());
                    userService.nextStep(chatId, Command.CHOOSING_FIAT_CURRENCY);
                    return;
                } else {
                    dealRepository.updateFiatCurrencyByPid(deal.getPid(), FiatCurrenciesUtil.getFirst());
                }
                messageService.sendMessageAndSaveMessageId(chatId, MessagePropertiesUtil.getChooseCurrency(DEAL_TYPE),
                        keyboardService.getCurrencies(DEAL_TYPE));
                userService.nextStep(chatId, Command.BUY_BITCOIN);
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
                    exchangeService.convertToRub(update,
                            userService.getCurrentDealByChatId(UpdateUtil.getChatId(update)));
                    return;
                }
                if (!exchangeService.saveSum(update)) return;
                responseSender.deleteMessage(UpdateUtil.getChatId(update), Integer.parseInt(userService.getBufferVariable(chatId)));
                if (dealService.getDealsCountByUserChatId(chatId) < 1) {
                    exchangeService.askForUserPromoCode(chatId, false);
                } else if (userService.getReferralBalanceByChatId(chatId) > 0) {
                    exchangeService.askForReferralDiscount(update);
                } else {
                    exchangeService.askForWallet(update);
                    userService.nextStep(chatId);
                }
                userService.nextStep(chatId);
                break;
            case 3:
                if (dealService.getDealsCountByUserChatId(chatId) < 1) {
                    exchangeService.processPromoCode(update);
                } else if (update.hasCallbackQuery()
                        && update.getCallbackQuery().getData().equals(ExchangeService.USE_REFERRAL_DISCOUNT)){
                    exchangeService.processReferralDiscount(update);
                }
                userService.nextStep(chatId);
                exchangeService.askForWallet(update);
                break;
            case 4:
                try {
                    exchangeService.saveWallet(update);
                } catch (BaseException e) {
                    responseSender.sendMessage(chatId, e.getMessage());
                    return;
                }
                exchangeService.askForPaymentType(update);
                userService.nextStep(chatId);
                responseSender.deleteMessage(UpdateUtil.getChatId(update), UpdateUtil.getMessage(update).getMessageId());
                break;
            case 5:
                Boolean result = exchangeService.savePaymentType(update);
                if (BooleanUtils.isTrue(result)) {
                    exchangeService.buildDeal(update);
                    userService.nextStep(chatId);
                } else if (BooleanUtils.isFalse(result)) responseSender.sendMessage(chatId, "Выберите способ оплаты.");
                break;
            case 6:
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
            case 7:
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

    public void previousStep(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        userService.previousStep(chatId);

        Long currentDealPid;
        switch (userService.getStepByChatId(chatId)) {
            case 1:
                messageService.sendMessageAndSaveMessageId(chatId, MessagePropertiesUtil.getChooseCurrency(DEAL_TYPE),
                        keyboardService.getCurrencies(DEAL_TYPE));;
                break;
            case 2:
                currentDealPid = userService.getCurrentDealByChatId(chatId);
                dealRepository.updateIsPersonalAppliedByPid(currentDealPid, false);
                exchangeServiceNew.askForSum(chatId,
                        dealService.getCryptoCurrencyByPid(currentDealPid), dealService.getDealTypeByPid(currentDealPid));
                break;
            case 3:
                if (dealService.getDealsCountByUserChatId(chatId) < 1) {
                    exchangeService.askForUserPromoCode(chatId, true);
                } else if (userService.getReferralBalanceByChatId(chatId) > 0) {
                    exchangeService.askForReferralDiscount(update);
                } else {
                    currentDealPid = userService.getCurrentDealByChatId(chatId);
                    dealRepository.updateIsPersonalAppliedByPid(currentDealPid, false);
                    exchangeServiceNew.askForSum(chatId,
                            dealService.getCryptoCurrencyByPid(currentDealPid), dealService.getDealTypeByPid(currentDealPid));
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
