package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.enums.CalculatorType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.ICalculatorTypeService;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.UpdateDispatcher;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.DealPromoUtil;
import tgb.btc.rce.util.FiatCurrencyUtil;
import tgb.btc.rce.util.UpdateUtil;

import javax.annotation.PostConstruct;
import java.util.Objects;

@CommandProcessor(command = Command.DEAL)
@Slf4j
public class DealProcessor extends Processor {

    public static final int AFTER_CALCULATOR_STEP = 3;

    private IUpdateDispatcher updateDispatcher;

    private ExchangeService exchangeService;

    private ICalculatorTypeService calculatorTypeService;

    private DealService dealService;

    @Autowired
    public void setDealService(DealService dealService) {
        this.dealService = dealService;
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
        Integer userStep = userRepository.getStepByChatId(chatId);
        boolean isBack = CallbackQueryUtil.isBack(update);
        if (!User.isDefault(userStep)) {
            if (isMainMenuCommand(update)) return;
            if (isBack) {
                userStep--;
                userRepository.previousStep(chatId);
                if (userStep == 0) {
                    processToStart(chatId, update);
                    return;
                }
                userStep--;
                userRepository.previousStep(chatId);
                responseSender.deleteCallbackMessageIfExists(update);
            }
        } else if (User.isDefault(userStep) && isBack) {
            processToStart(chatId, update);
            return;
        }
       switchByStep(update, chatId, userStep, isBack);
    }

    private void switchByStep(Update update, Long chatId, Integer userStep, boolean isBack) {
        switch (userStep) {
            case 0:
                if (!isBack) userRepository.updateCommandByChatId(Command.DEAL, chatId);
                userRepository.nextStep(chatId);
                if (!FiatCurrencyUtil.isFew()) {
                    userRepository.nextStep(chatId);
                    run(update);
                    break;
                }
                exchangeService.askForFiatCurrency(chatId);
                break;
            case 1:
                if (!isBack) {
                    responseSender.deleteCallbackMessageIfExists(update);
                    exchangeService.saveFiatCurrency(update);
                }
                exchangeService.askForCryptoCurrency(chatId);
                userRepository.nextStep(chatId);
                break;
            case 2:
                if (!isBack) {
                    responseSender.deleteCallbackMessageIfExists(update);
                    exchangeService.saveCryptoCurrency(update);
                }
                calculatorTypeService.run(update);
                break;
            case 3:
                if (DealPromoUtil.isNone() && dealService.isFirstDeal(chatId)) {
                    if (isBack) userRepository.previousStep(chatId);
                    else userRepository.nextStep(chatId);
                    switchByStep(update, chatId, userStep, isBack);
                    break;
                }
                exchangeService.askForUserPromoCode(chatId);
                break;
        }
    }

    private void processToStart(Long chatId, Update update) {
        responseSender.deleteCallbackMessageIfExists(update);
        updateDispatcher.runProcessor(Command.START, chatId, update);
    }

    private boolean isMainMenuCommand(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Command commandFromUpdate = Command.fromUpdate(update);
        Command mainMenuCommand = null;
        for (Command command : Menu.MAIN.getCommands()) {
            if (command.equals(commandFromUpdate)) mainMenuCommand = command;
        }
        if (Objects.isNull(mainMenuCommand)) return false;
        userRepository.setDefaultValues(chatId);
        userService.deleteCurrentDeal(chatId);
        responseSender.deleteCallbackMessageIfExists(update);
        updateDispatcher.runProcessor(mainMenuCommand, chatId, update);
        return true;
    }
}
