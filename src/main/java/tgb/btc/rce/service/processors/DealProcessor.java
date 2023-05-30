package tgb.btc.rce.service.processors;

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
import tgb.btc.rce.service.impl.UpdateDispatcher;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.util.FiatCurrencyUtil;
import tgb.btc.rce.util.UpdateUtil;

import javax.annotation.PostConstruct;
import java.util.Objects;

@CommandProcessor(command = Command.DEAL)
public class DealProcessor extends Processor {

    private IUpdateDispatcher updateDispatcher;

    private ExchangeService exchangeService;

    private ICalculatorTypeService calculatorTypeService;

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
        boolean isDefaultStep = User.DEFAULT_STEP == userStep;
        if (!isDefaultStep && isMainMenuCommand(update)) return;
        if (!isDefaultStep && isBack(update)) userRepository.previousStep(chatId);
        else if (isDefaultStep && isBack(update)) updateDispatcher.runProcessor(Command.START, chatId, update);
        switch (userStep) {
            case 0:
                userRepository.nextStep(chatId, Command.DEAL);
                if (!FiatCurrencyUtil.isFew()) {
                    userRepository.nextStep(chatId);
                    run(update);
                    break;
                }
                exchangeService.askForFiatCurrency(chatId);
                break;
            case 1:
                responseSender.deleteCallbackMessageIfExists(update);
                exchangeService.saveFiatCurrency(update);
                exchangeService.askForCryptoCurrency(chatId);
                userRepository.nextStep(chatId);
                break;
            case 2:
                responseSender.deleteCallbackMessageIfExists(update);
                exchangeService.saveCryptoCurrency(update);
                calculatorTypeService.run(update);
                break;
        }
    }

    private boolean isBack(Update update) {
        return update.hasCallbackQuery() && Command.BACK.getText().equals(update.getCallbackQuery().getData());
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
        updateDispatcher.runProcessor(mainMenuCommand, chatId, update);
        return true;
    }
}
