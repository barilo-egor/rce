package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.constants.enums.DeliveryKind;
import tgb.btc.library.constants.enums.bot.BotMessageType;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.interfaces.service.bean.bot.IBotMessageService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealPropertyService;
import tgb.btc.library.service.bean.bot.BotMessageService;
import tgb.btc.library.util.FiatCurrencyUtil;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.ICalculatorTypeService;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.process.IDealProcessService;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@CommandProcessor(command = Command.DEAL)
@Slf4j
public class DealProcessor extends Processor {

    public static final int AFTER_CALCULATOR_STEP = 3;

    private IUpdateDispatcher updateDispatcher;

    private ExchangeService exchangeService;

    private ICalculatorTypeService calculatorTypeService;

    private IDealPropertyService dealPropertyService;

    private IReadDealService readDealService;

    private IModifyDealService modifyDealService;

    private IBotMessageService botMessageService;

    private IDealProcessService dealProcessService;

    @Autowired
    public void setReadDealService(IReadDealService readDealService) {
        this.readDealService = readDealService;
    }

    @Autowired
    public void setModifyDealService(IModifyDealService modifyDealService) {
        this.modifyDealService = modifyDealService;
    }

    @Autowired
    public void setDealPropertyService(IDealPropertyService dealPropertyService) {
        this.dealPropertyService = dealPropertyService;
    }

    @Autowired
    public void setDealProcessService(IDealProcessService dealProcessService) {
        this.dealProcessService = dealProcessService;
    }

    @Autowired
    public void setBotMessageService(BotMessageService botMessageService) {
        this.botMessageService = botMessageService;
    }

    @Autowired
    public void setCalculatorTypeService(ICalculatorTypeService calculatorTypeService) {
        this.calculatorTypeService = calculatorTypeService;
    }

    @Autowired
    public void setExchangeService(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @Autowired
    public void setUpdateDispatcher(IUpdateDispatcher updateDispatcher) {
        this.updateDispatcher = updateDispatcher;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Integer userStep = readUserService.getStepByChatId(chatId);
        boolean isBack = CallbackQueryUtil.isBack(update);
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
    }

    private void switchByStep(Update update, Long chatId, Integer userStep, boolean isBack) {
        DealType dealType;
        switch (userStep) {
            case 0:
                if (!isBack) modifyUserService.updateCommandByChatId(Command.DEAL.name(), chatId);
                if (!FiatCurrencyUtil.isFew()) {
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
                Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
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
                if (exchangeService.isFewPaymentTypes(chatId)) {
                    modifyUserService.nextStep(chatId);
                    exchangeService.askForPaymentType(update);
                    break;
                }
                recursiveSwitch(update, chatId, isBack);
                break;
            case 6:
                if (!isBack) {
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
                if (DeliveryKind.STANDARD.isCurrent() && DealType.isBuy(dealType) && CryptoCurrency.BITCOIN.equals(cryptoCurrency)) {
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
                    updateDispatcher.runProcessor(Command.START, chatId, update);
                    return;
                }
                if (!exchangeService.saveReceipts(update)) return;
                exchangeService.confirmDeal(update);
                responseSender.sendBotMessage(botMessageService.findByTypeNullSafe(BotMessageType.START), chatId);
                processToMainMenu(chatId);
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
        return update.hasMessage() && Command.RECEIPTS_CANCEL_DEAL.getText().equals(UpdateUtil.getMessageText(update));
    }

    private boolean hasCheck(Update update) {
        return !update.hasMessage() || (!update.getMessage().hasPhoto() || !update.getMessage().hasDocument());
    }

    private void processToStart(Long chatId, Update update) {
        responseSender.deleteCallbackMessageIfExists(update);
        updateDispatcher.runProcessor(Command.START, chatId, update);
    }

    public boolean isMainMenuCommand(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return false;
        Long chatId = UpdateUtil.getChatId(update);
        Command commandFromUpdate = Command.fromUpdate(update);
        Command mainMenuCommand = null;
        Set<Command> commands = new HashSet<>(Menu.MAIN.getCommands());
        commands.add(Command.ADMIN_PANEL);
        commands.add(Command.OPERATOR_PANEL);
        commands.add(Command.START);
        for (Command command : commands) {
            if (command.equals(commandFromUpdate)) mainMenuCommand = command;
        }
        if (Objects.isNull(mainMenuCommand)) return false;
        modifyUserService.setDefaultValues(chatId);
        Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
        if (Objects.nonNull(currentDealPid)) {
            if (readDealService.existsById(currentDealPid)) {
                log.info("Сделка " + currentDealPid + " удалена по команде главного меню.");
                modifyDealService.deleteById(currentDealPid);
            }
            modifyUserService.updateCurrentDealByChatId(null, chatId);
        }
        responseSender.deleteCallbackMessageIfExists(update);
        updateDispatcher.runProcessor(mainMenuCommand, chatId, update);
        return true;
    }
}
