package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.service.process.RPSService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.KeyboardService;
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
        Integer referralBalance;
        switch (userRepository.getStepByChatId(chatId)) {
            case 0:
                referralBalance = userService.getReferralBalanceByChatId(chatId);
                responseSender.sendMessage(chatId,
                        String.format(RPS_MESSAGE.getString("start"),
                                String.format(RPS_MESSAGE.getString("referral.balance"), referralBalance)),
                        keyboardService.getRPSRates());
                userRepository.updateCommandByChatId(Command.RPS.name(), chatId);
                userRepository.updateStepByChatId(chatId, 1);
                break;
            case 1:
                 query = update.getCallbackQuery();
                if (Objects.nonNull(query)) {
                    responseSender.deleteCallbackMessageIfExists(update);
                    if (!query.getData().equals(PropertiesPath.RPS_MESSAGE.getString("close"))) {
                        localCache.put(chatId, query.getData());
                        responseSender.sendMessage(chatId, RPS_MESSAGE.getString("ask"),keyboardService.getRPSElements());
                        userRepository.updateStepByChatId(chatId, 2);
                    }
                }
                break;
            case 2:
                query = update.getCallbackQuery();
                if (Objects.nonNull(query)) {
                    responseSender.deleteCallbackMessageIfExists(update);
                    StringBuilder sb = new StringBuilder(rpsService.getResultMessageText(query.getData(), localCache.get(chatId), chatId));
                    referralBalance = userService.getReferralBalanceByChatId(chatId);
                    sb.append(System.lineSeparator())
                            .append(String.format(RPS_MESSAGE.getString("referral.balance"), referralBalance));
                    responseSender.sendMessage(chatId, sb.toString());
                } else {
                    responseSender.sendMessage(chatId, RPS_MESSAGE.getString("ask"),keyboardService.getRPSElements());
                }
        }
    }

}
