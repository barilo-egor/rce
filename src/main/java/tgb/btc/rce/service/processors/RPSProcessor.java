package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.util.UpdateUtil;

import java.util.Objects;

import static tgb.btc.library.constants.enums.properties.PropertiesPath.RPS_MESSAGE;

@CommandProcessor(command = Command.RPS)
public class RPSProcessor extends Processor {

    private KeyboardService keyboardService;

    @Autowired
    public void setKeyboardService(KeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        switch (userRepository.getStepByChatId(chatId)) {
            case 0:
                responseSender.sendMessage(chatId, RPS_MESSAGE.getString("start"), keyboardService.getRPSRates());
                userRepository.updateCommandByChatId(Command.RPS.name(), chatId);
                userRepository.updateStepByChatId(chatId, 1);
                break;
            case 1:
                CallbackQuery query = update.getCallbackQuery();
                if (Objects.nonNull(query)) {
                    String data = query.getData();
                    responseSender.deleteCallbackMessageIfExists(update);
                    if (!data.equals(PropertiesPath.RPS_MESSAGE.getString("close"))) {
                        responseSender.sendMessage(chatId, RPS_MESSAGE.getString("ask"),keyboardService.getRPSElements());
                    } else {

                    }
                } else {
                    responseSender.sendMessage(chatId, RPS_MESSAGE.getString("start"), keyboardService.getRPSRates());
                }
        }
    }

}
