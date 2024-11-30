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
import tgb.btc.rce.enums.MessageImage;
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
import tgb.btc.rce.service.impl.util.TextTextCommandService;
import tgb.btc.rce.service.process.IDealBotProcessService;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
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

    private final IReadDealService readDealService;

    private final IModifyDealService modifyDealService;

    private final IDealBotProcessService dealProcessService;

    private final IFiatCurrencyService fiatCurrencyService;

    private final IModule<DeliveryKind> deliveryKindModule;

    private final ISecurePaymentDetailsService securePaymentDetailsService;

    private final ApplicationEventPublisher eventPublisher;

    private final IStartService startService;

    private final IMessageImageResponseSender messageImageResponseSender;

    private final IUpdateService updateService;

    private final IReadUserService readUserService;

    private final ICallbackDataService callbackDataService;

    private final IModifyUserService modifyUserService;

    private final IResponseSender responseSender;

    private final IUserCommonService userCommonService;

    private final IRedisUserStateService redisUserStateService;

    private final TextTextCommandService textCommandService;

    public DealHandler(ExchangeService exchangeService, ICalculatorTypeService calculatorTypeService,
                       IDealPropertyService dealPropertyService, IReadDealService readDealService,
                       IModifyDealService modifyDealService, IDealBotProcessService dealProcessService,
                       IFiatCurrencyService fiatCurrencyService, IModule<DeliveryKind> deliveryKindModule,
                       ISecurePaymentDetailsService securePaymentDetailsService,
                       ApplicationEventPublisher eventPublisher, IStartService startService,
                       IMessageImageResponseSender messageImageResponseSender, IUpdateService updateService,
                       IReadUserService readUserService, ICallbackDataService callbackDataService,
                       IModifyUserService modifyUserService, IResponseSender responseSender,
                       IUserCommonService userCommonService, IRedisUserStateService redisUserStateService,
                       TextTextCommandService textCommandService) {
        this.exchangeService = exchangeService;
        this.calculatorTypeService = calculatorTypeService;
        this.dealPropertyService = dealPropertyService;
        this.readDealService = readDealService;
        this.modifyDealService = modifyDealService;
        this.dealProcessService = dealProcessService;
        this.fiatCurrencyService = fiatCurrencyService;
        this.deliveryKindModule = deliveryKindModule;
        this.securePaymentDetailsService = securePaymentDetailsService;
        this.eventPublisher = eventPublisher;
        this.startService = startService;
        this.messageImageResponseSender = messageImageResponseSender;
        this.updateService = updateService;
        this.readUserService = readUserService;
        this.callbackDataService = callbackDataService;
        this.modifyUserService = modifyUserService;
        this.responseSender = responseSender;
        this.userCommonService = userCommonService;
        this.redisUserStateService = redisUserStateService;
        this.textCommandService = textCommandService;
    }

    @Override
    public void handle(Update update) {
        long t1 = System.currentTimeMillis();
        Long chatId = updateService.getChatId(update);
        Integer userStep = readUserService.getStepByChatId(chatId);
        boolean isBack = update.hasCallbackQuery() && callbackDataService.isCallbackQueryData(CallbackQueryData.BACK, update.getCallbackQuery().getData());
        if (!User.isDefault(userStep)) {
            if (isMainMenuCommand(update)) return;
            if (isBack) {
                userStep--;
                modifyUserService.previousStep(chatId);
                if (userStep == 0) {
                    processToStart(chatId, update);
                    return;
                }
                userStep--;
                modifyUserService.previousStep(chatId);
                responseSender.deleteCallbackMessageIfExists(update);
            }
        } else if (User.isDefault(userStep) && isBack) {
            processToStart(chatId, update);
            return;
        }
        switchByStep(update, chatId, userStep, isBack);
        long t2 = System.currentTimeMillis();
        log.debug("Обработка DEAL = {} ms", (t2 - t1));
    }

    private void switchByStep(Update update, Long chatId, Integer userStep, boolean isBack) {
        DealType dealType;
        Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
        switch (userStep) {
            case 0:
                if (!isBack) redisUserStateService.save(chatId, UserState.DEAL);
                if (!fiatCurrencyService.isFew()) {
                    recursiveSwitch(update, chatId, isBack);
                    break;
                }
                modifyUserService.nextStep(chatId);
                exchangeService.askForFiatCurrency(chatId);
                break;
            case 1:
                if (!isBack) {
                    responseSender.deleteCallbackMessageIfExists(update);
                    if (!exchangeService.saveFiatCurrency(update)) return;
                }
                exchangeService.askForCryptoCurrency(chatId);
                modifyUserService.nextStep(chatId);
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
                modifyUserService.nextStep(chatId);
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
                modifyUserService.nextStep(chatId);
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
                    modifyUserService.nextStep(chatId);
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
                modifyUserService.nextStep(chatId);
                break;
            case 7:
                if (!isBack) {
                    if (!exchangeService.saveRequisites(update)) return;
                }
                Long dealPid = readUserService.getCurrentDealByChatId(chatId);
                dealType = dealPropertyService.getDealTypeByPid(dealPid);
                CryptoCurrency cryptoCurrency = dealPropertyService.getCryptoCurrencyByPid(dealPid);
                if (deliveryKindModule.isCurrent(DeliveryKind.STANDARD) && DealType.isBuy(dealType) && CryptoCurrency.BITCOIN.equals(cryptoCurrency)) {
                    modifyUserService.nextStep(chatId);
                    exchangeService.askForDeliveryType(chatId, dealPropertyService.getFiatCurrencyByPid(dealPid),dealType, cryptoCurrency);
                    break;
                }
                recursiveSwitch(update, chatId, isBack);
                break;
            case 8:
                if (!isBack) {
                    exchangeService.saveDeliveryTypeAndUpdateAmount(update);
                }
                modifyUserService.nextStep(chatId);
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
                if (!hasCheck(update)) {
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
        if (isBack) modifyUserService.previousStep(chatId);
        else modifyUserService.nextStep(chatId);
        switchByStep(update, chatId, readUserService
                .getStepByChatId(chatId), isBack);
    }

    private boolean isReceiptsCancel(Update update) {
        return update.hasMessage() && TextCommand.RECEIPTS_CANCEL_DEAL.getText().equals(updateService.getMessageText(update));
    }

    private boolean hasCheck(Update update) {
        return !update.hasMessage() || (!update.getMessage().hasPhoto() || !update.getMessage().hasDocument());
    }

    private void processToStart(Long chatId, Update update) {
        responseSender.deleteCallbackMessageIfExists(update);
        startService.process(chatId);
    }

    public boolean isMainMenuCommand(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return false;
        Long chatId = update.getMessage().getChatId();
        boolean isMainMenu = false;
        Set<String> commands = Menu.MAIN.getTextCommands().stream()
                .map(textCommandService::getText)
                .collect(Collectors.toSet());
        commands.add(SlashCommand.START.getText());
        for (String command : commands) {
            if (command.equals(update.getMessage().getText())) {
                isMainMenu = true;
                break;
            }
        }
        if (!isMainMenu) return false;
        redisUserStateService.delete(chatId);
        modifyUserService.setDefaultValues(chatId);
        Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
        if (Objects.nonNull(currentDealPid)) {
            if (readDealService.existsById(currentDealPid)) {
                log.info("Сделка {} удалена по команде главного меню.", currentDealPid);
                modifyDealService.deleteById(currentDealPid);
            }
            modifyUserService.updateCurrentDealByChatId(null, chatId);
        }
        responseSender.deleteCallbackMessageIfExists(update);
        TelegramUpdateEvent telegramUpdateEvent = new TelegramUpdateEvent(this, update);
        eventPublisher.publishEvent(telegramUpdateEvent);
        return true;
    }

    @Override
    public UserState getUserState() {
        return UserState.DEAL;
    }
}
