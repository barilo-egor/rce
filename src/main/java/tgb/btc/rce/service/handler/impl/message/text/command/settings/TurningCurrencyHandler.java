package tgb.btc.rce.service.handler.impl.message.text.command.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.handler.util.ITurningCurrencyService;

@Service
public class TurningCurrencyHandler implements ITextCommandHandler {

    private final ITurningCurrencyService turningCurrencyService;

    public TurningCurrencyHandler(ITurningCurrencyService turningCurrencyService) {
        this.turningCurrencyService = turningCurrencyService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        turningCurrencyService.process(chatId);
    }



    @Override
    public TextCommand getTextCommand() {
        return TextCommand.TURNING_CURRENCY;
    }
}
