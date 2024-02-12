package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.service.process.RPSService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static tgb.btc.library.constants.enums.properties.PropertiesPath.RPS_MESSAGE;

@CommandProcessor(command = Command.RPS)
public class RPSProcessor extends Processor {

    private KeyboardService keyboardService;

    private RPSService rpsService;

    public static ConcurrentHashMap<Long, String> localCache = new ConcurrentHashMap<>();

    @Autowired
    public void setKeyboardService(KeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    @Autowired
    public void setRpsService(RPSService rpsService) {
        this.rpsService = rpsService;
    }


    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        CallbackQuery query;
        Integer userStep = userRepository.getStepByChatId(chatId);
        boolean isBack = CallbackQueryUtil.isBack(update);
        if (isBack) {
            userStep--;
            userRepository.previousStep(chatId);
            if (userStep == 0) {
                responseSender.deleteCallbackMessageButtonsIfExists(update);
                userRepository.updateCommandByChatId(Command.DRAWS.name(), chatId);
                return;
            } else {
                responseSender.deleteCallbackMessageIfExists(update);
            }
            userRepository.previousStep(chatId);
        }
        switch (userRepository.getStepByChatId(chatId)) {
            case 0:
                if (!isBack) {
                    sendStartMessage(chatId);
                }
                sendRatesMessage(chatId);
                userRepository.updateCommandByChatId(Command.RPS.name(), chatId);
                userRepository.updateStepByChatId(chatId, 1);
                break;
            case 1:
                 query = update.getCallbackQuery();
                if (Objects.nonNull(query)) {
                    if (!CallbackQueryUtil.isBack(update)) {
                        localCache.put(chatId, query.getData());
                        responseSender.deleteCallbackMessageIfExists(update);
                    }
                    sendAskMessage(chatId);
                    userRepository.updateStepByChatId(chatId, 2);
                }
                break;
            case 2:
                query = update.getCallbackQuery();
                if (Objects.nonNull(query)) {
                    sendResultMessage(chatId, query.getData());
                    sendStartMessage(chatId);
                    sendRatesMessage(chatId);
                    userRepository.updateStepByChatId(chatId, 1);
                } else {
                    sendAskMessage(chatId);
                }
        }
    }

    private void sendAskMessage(Long chatId) {
        responseSender.sendMessage(chatId, RPS_MESSAGE.getString("ask"),keyboardService.getRPSElements());
    }

    private void sendStartMessage(Long chatId) {
        String sb = RPS_MESSAGE.getString("start") + System.lineSeparator() +
                RPS_MESSAGE.getString("referral.balance") + " " +
                userService.getReferralBalanceByChatId(chatId) + "₽";
        responseSender.sendMessage(chatId, sb);
    }

    private void sendRatesMessage(Long chatId) {
        responseSender.sendMessage(chatId, RPS_MESSAGE.getString("select.rate"), keyboardService.getRPSRates());
    }

    private void sendResultMessage(Long chatId, String elementName) {
        String sb = rpsService.getResultMessageText(elementName, localCache.get(chatId), chatId);
        responseSender.sendMessage(chatId, sb);
    }

}
