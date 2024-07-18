package tgb.btc.rce.service.processors.admin.settings.paymenttypes.requisite.dynamic;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.util.IFiatCurrencyService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;


@CommandProcessor(command = Command.TURN_DYNAMIC_REQUISITES)
public class FiatCurrencyDynamicRequisite extends Processor {

    private TurnDynamicRequisites turnDynamicRequisites;

    private IFiatCurrencyService fiatCurrencyService;

    @Autowired
    public void setFiatCurrencyService(IFiatCurrencyService fiatCurrencyService) {
        this.fiatCurrencyService = fiatCurrencyService;
    }

    @Autowired
    public void setTurnDynamicRequisites(TurnDynamicRequisites turnDynamicRequisites) {
        this.turnDynamicRequisites = turnDynamicRequisites;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        if (fiatCurrencyService.isFew()) {
            responseSender.sendMessage(chatId, BotStringConstants.FIAT_CURRENCY_CHOOSE, keyboardService.getFiatCurrenciesKeyboard());
            modifyUserService.nextStep(chatId, Command.TURN_DYNAMIC_REQUISITES.name());
        } else {
            turnDynamicRequisites.run(update);
        }
    }
}
