package tgb.btc.rce.service.handler.impl.message.text.command.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@Service
public class TurnRankDiscountHandler implements ITextCommandHandler {

    private final VariablePropertiesReader variablePropertiesReader;

    private final IResponseSender responseSender;

    private final IKeyboardBuildService keyboardBuildService;

    public TurnRankDiscountHandler(VariablePropertiesReader variablePropertiesReader, IResponseSender responseSender,
                                   IKeyboardBuildService keyboardBuildService) {
        this.variablePropertiesReader = variablePropertiesReader;
        this.responseSender = responseSender;
        this.keyboardBuildService = keyboardBuildService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        boolean isOn = variablePropertiesReader.getBoolean(VariableType.DEAL_RANK_DISCOUNT_ENABLE);
        String messageText = isOn ? "Ранговая скидка включена для всех. Выключить?" : "Ранговая скидка выключена для всех. Включить?";
        String text = isOn ? "Выключить" : "Включить";
        String data = Command.TURNING_RANK_DISCOUNT.name() + BotStringConstants.CALLBACK_DATA_SPLITTER + !isOn;
        responseSender.sendMessage(chatId, messageText, keyboardBuildService.buildInline(List.of(InlineButton.builder()
                .text(text)
                .data(data)
                .build())));
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.TURN_RANK_DISCOUNT;
    }
}
