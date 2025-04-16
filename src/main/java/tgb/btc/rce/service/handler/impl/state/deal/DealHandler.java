package tgb.btc.rce.service.handler.impl.state.deal;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.constants.enums.DeliveryKind;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.library.interfaces.service.bean.bot.ISecurePaymentDetailsService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealPropertyService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.service.bean.common.bot.IUserCommonService;
import tgb.btc.library.interfaces.util.IFiatCurrencyService;
import tgb.btc.rce.enums.Menu;
import tgb.btc.library.interfaces.enums.MessageImage;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IMessageImageResponseSender;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.ICalculatorTypeService;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IStartService;
import tgb.btc.rce.service.impl.util.MenuService;
import tgb.btc.rce.service.impl.util.TextTextCommandService;
import tgb.btc.rce.service.process.IDealBotProcessService;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.service.util.IMenuService;
import tgb.btc.rce.vo.TelegramUpdateEvent;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DealHandler implements IStateHandler {

    public static final int AFTER_CALCULATOR_STEP = 3;

    private final ExchangeService exchangeService;

    private final ICalculatorTypeService calculatorTypeService;

    private final IDealPropertyService dealPropertyService;

    private final IDealBotProcessService dealProcessService;

    private final IFiatCurrencyService fiatCurrencyService;

    private final IModule<DeliveryKind> deliveryKindModule;

    private final ISecurePaymentDetailsService securePaymentDetailsService;

    private final IStartService startService;

    private final IMessageImageResponseSender messageImageResponseSender;

    private final IUpdateService updateService;

    private final IReadUserService readUserService;

    private final ICallbackDataService callbackDataService;

    private final IResponseSender responseSender;

    private final IUserCommonService userCommonService;

    private final IRedisStringService redisStringService;

    public DealHandler(ExchangeService exchangeService, ICalculatorTypeService calculatorTypeService,
                       IDealPropertyService dealPropertyService, IDealBotProcessService dealProcessService,
                       IFiatCurrencyService fiatCurrencyService, IModule<DeliveryKind> deliveryKindModule,
                       ISecurePaymentDetailsService securePaymentDetailsService, IStartService startService,
                       IMessageImageResponseSender messageImageResponseSender, IUpdateService updateService,
                       IReadUserService readUserService, ICallbackDataService callbackDataService,
                       IResponseSender responseSender,
                       IUserCommonService userCommonService, IRedisStringService redisStringService) {
        this.exchangeService = exchangeService;
        this.calculatorTypeService = calculatorTypeService;
        this.dealPropertyService = dealPropertyService;
        this.dealProcessService = dealProcessService;
        this.fiatCurrencyService = fiatCurrencyService;
        this.deliveryKindModule = deliveryKindModule;
        this.securePaymentDetailsService = securePaymentDetailsService;
        this.startService = startService;
        this.messageImageResponseSender = messageImageResponseSender;
        this.updateService = updateService;
        this.readUserService = readUserService;
        this.callbackDataService = callbackDataService;
        this.responseSender = responseSender;
        this.userCommonService = userCommonService;
        this.redisStringService = redisStringService;
    }

    @Override
    public void handle(Update update) {
        Long chatId = updateService.getChatId(update);
        Integer userStep = redisStringService.getStep(chatId);
        boolean isBack = callbackDataService.isBack(update);
        if (isBack) {
            if (userStep == 1) {
                processToStart(chatId, update);
                return;
            }
            userStep -= 2;
            redisStringService.saveStep(chatId, userStep);
            responseSender.deleteCallbackMessageIfExists(update);
        }
        switchByStep(update, chatId, userStep, isBack);
    }

    private void switchByStep(Update update, Long chatId, Integer userStep, boolean isBack) {
        DealType dealType;
        Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
        switch (userStep) {
            case 0:
                if (!fiatCurrencyService.isFew()) {
                    recursiveSwitch(update, chatId, isBack);
                    break;
                }
                redisStringService.nextStep(chatId);
                exchangeService.askForFiatCurrency(chatId);
                break;
            case 1:
                if (!isBack) {
                    responseSender.deleteCallbackMessageIfExists(update);
                    if (!exchangeService.saveFiatCurrency(update)) return;
                }
                exchangeService.askForCryptoCurrency(chatId);
                redisStringService.nextStep(chatId);
                break;
            case 2:
                if (!isBack) {
                    responseSender.deleteCallbackMessageIfExists(update);
                    if (!exchangeService.saveCryptoCurrency(update)) return;
                }
                calculatorTypeService.run(update);
                break;
            case 3:
                dealType = dealPropertyService.getDealTypeByPid(readUserService.getCurrentDealByChatId(chatId));
                if (!isBack) exchangeService.sendTotalDealAmount(chatId, dealType, dealPropertyService.getCryptoCurrencyByPid(currentDealPid),
                        dealPropertyService.getFiatCurrencyByPid(currentDealPid));
                if (!DealType.isBuy(dealType) || !dealProcessService.isAvailableForPromo(chatId)) {
                    recursiveSwitch(update, chatId, isBack);
                    break;
                }
                exchangeService.askForUserPromoCode(chatId);
                redisStringService.nextStep(chatId);
                break;
            case 4:
                dealType = dealPropertyService.getDealTypeByPid(readUserService.getCurrentDealByChatId(chatId));
                if (!isBack && DealType.isBuy(dealType) && dealProcessService.isAvailableForPromo(chatId)) {
                    responseSender.deleteCallbackMessageIfExists(update);
                    exchangeService.processPromoCode(update);
                }
                if (!DealType.isBuy(dealType) || userCommonService.isReferralBalanceEmpty(chatId)) {
                    recursiveSwitch(update, chatId, isBack);
                    break;
                }
                redisStringService.nextStep(chatId);
                exchangeService.askForReferralDiscount(update);
                break;
            case 5:
                dealType = dealPropertyService.getDealTypeByPid(readUserService.getCurrentDealByChatId(chatId));
                if (!isBack && DealType.isBuy(dealType) && !userCommonService.isReferralBalanceEmpty(chatId)) {
                    if (!exchangeService.processReferralDiscount(update)) return;
                }
                responseSender.deleteCallbackMessageIfExists(update);
                if (exchangeService.isFewPaymentTypes(chatId)
                        && (!DealType.isBuy(dealType)
                        || securePaymentDetailsService.hasAccessToPaymentTypes(chatId, dealPropertyService.getFiatCurrencyByPid(currentDealPid)))) {
                    redisStringService.nextStep(chatId);
                    exchangeService.askForPaymentType(update);
                    break;
                }
                recursiveSwitch(update, chatId, isBack);
                break;
            case 6:
                dealType = dealPropertyService.getDealTypeByPid(readUserService.getCurrentDealByChatId(chatId));
                if (!isBack && (!DealType.isBuy(dealType)
                        || securePaymentDetailsService.hasAccessToPaymentTypes(chatId, dealPropertyService.getFiatCurrencyByPid(currentDealPid)))) {
                    if (!exchangeService.savePaymentType(update)) return;
                }
                exchangeService.askForUserRequisites(update);
                redisStringService.nextStep(chatId);
                break;
            case 7:
                if (!isBack) {
                    if (!exchangeService.saveRequisites(update)) return;
                }
                Long dealPid = readUserService.getCurrentDealByChatId(chatId);
                dealType = dealPropertyService.getDealTypeByPid(dealPid);
                CryptoCurrency cryptoCurrency = dealPropertyService.getCryptoCurrencyByPid(dealPid);
                if (deliveryKindModule.isCurrent(DeliveryKind.STANDARD) && DealType.isBuy(dealType) && CryptoCurrency.BITCOIN.equals(cryptoCurrency)) {
                    redisStringService.nextStep(chatId);
                    exchangeService.askForDeliveryType(chatId, dealPropertyService.getFiatCurrencyByPid(dealPid),dealType, cryptoCurrency);
                    break;
                }
                recursiveSwitch(update, chatId, isBack);
                break;
            case 8:
                if (!isBack) {
                    exchangeService.saveDeliveryTypeAndUpdateAmount(update);
                }
                redisStringService.nextStep(chatId);
                exchangeService.buildDeal(update);
                break;
            case 9:
                Boolean result = exchangeService.isPaid(update);
                if (Objects.isNull(result)) {
                    return;
                }
                if (BooleanUtils.isFalse(result)) processToStart(chatId, update);
                break;
            case 10:
                if (!updateService.hasDocumentOrPhoto(update)) {
                    exchangeService.askForReceipts(update);
                    return;
                }
                if (isReceiptsCancel(update)) {
                    exchangeService.cancelDeal(update.getMessage().getMessageId(), chatId,
                            readUserService.getCurrentDealByChatId(chatId));
                    startService.process(chatId);
                    return;
                }
                if (!exchangeService.saveReceipts(update)) return;
                exchangeService.confirmDeal(update);
                messageImageResponseSender.sendMessage(MessageImage.START, chatId);
                startService.processToMainMenu(chatId);
                break;
        }
    }

    private void recursiveSwitch(Update update, Long chatId, boolean isBack) {
        if (isBack) redisStringService.previousStep(chatId);
        else redisStringService.nextStep(chatId);
        switchByStep(update, chatId, redisStringService.getStep(chatId), isBack);
    }

    private boolean isReceiptsCancel(Update update) {
        return update.hasMessage() && TextCommand.RECEIPTS_CANCEL_DEAL.getText().equals(updateService.getMessageText(update));
    }

    private void processToStart(Long chatId, Update update) {
        responseSender.deleteCallbackMessageIfExists(update);
        startService.process(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.DEAL;
    }
}
