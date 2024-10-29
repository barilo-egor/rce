package tgb.btc.rce.service.processors.admin.settings.disounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.TURN_RANK_DISCOUNT)
public class TurnRankDiscount extends Processor {

    private VariablePropertiesReader variablePropertiesReader;

    @Autowired
    public void setVariablePropertiesReader(VariablePropertiesReader variablePropertiesReader) {
        this.variablePropertiesReader = variablePropertiesReader;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        boolean isOn = variablePropertiesReader.getBoolean(VariableType.DEAL_RANK_DISCOUNT_ENABLE);
        String message = isOn ? "Ранговая скидка включена для всех. Выключить?" : "Ранговая скидка выключена для всех. Включить?";
        String text = isOn ? "Выключить" : "Включить";
        String data = Command.TURNING_RANK_DISCOUNT.name() + BotStringConstants.CALLBACK_DATA_SPLITTER + !isOn;
        responseSender.sendMessage(chatId, message, keyboardBuildService.buildInline(List.of(InlineButton.builder()
                                                                                             .text(text)
                                                                                             .data(data)
                                                                                             .build())));
    }

}
