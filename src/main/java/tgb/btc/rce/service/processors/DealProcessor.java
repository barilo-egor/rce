package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.util.FiatCurrencyUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.util.Objects;

@CommandProcessor(command = Command.DEAL)
public class DealProcessor extends Processor {

    private IUpdateDispatcher updateDispatcher;

    private ExchangeService exchangeService;

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
        if (isMainMenuCommand(update)) return;
        Integer userStep = userRepository.getStepByChatId(chatId);
        boolean isDefaultStep = User.DEFAULT_STEP == userStep;
        if (!isDefaultStep && isBack(update)) userRepository.previousStep(chatId);
        else if (isDefaultStep && isBack(update)) updateDispatcher.runProcessor(Command.START, chatId, update);
        switch (userStep) {
            case 0:
                userRepository.nextStep(chatId, Command.DEAL);
                if (!FiatCurrencyUtil.isFew()) {
                    userRepository.nextStep(chatId);
                    run(update);
                }
                exchangeService.askForFiatCurrency(chatId);
                break;
            case 1:
                exchangeService.saveFiatCurrency(chatId, FiatCurrency.fromCallbackQuery(update.getCallbackQuery()));

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
