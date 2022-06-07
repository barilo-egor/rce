package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.BotMessageType;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.BotMessageService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.util.Objects;

@CommandProcessor(command = Command.LOTTERY)
@Slf4j
public class Lottery extends Processor {

    private final BotMessageService botMessageService;

    @Autowired
    public Lottery(IResponseSender responseSender, UserService userService, BotMessageService botMessageService) {
        super(responseSender, userService);
        this.botMessageService = botMessageService;
    }

    @Override
    public void run(Update update) {
        User user = userService.findByChatId(update);
        if(Objects.isNull(user.getLotteryCount()) || user.getLotteryCount() == 0) {
            responseSender.sendMessage(user.getChatId(),
                    MessagePropertiesUtil.getMessage(PropertiesMessage.NO_LOTTERY_ATTEMPTS));
            return;
        }
        processLottery(update, user);
    }

    private void processLottery(Update update, User user) {
        float probability = BotVariablePropertiesUtil.getFloat(BotVariableType.PROBABILITY);
        if (getRandomBoolean(probability)) {
            responseSender.sendBotMessage(botMessageService.findByType(BotMessageType.WON_LOTTERY), user.getChatId(),
                    MenuFactory.getLink(BotStringConstants.WRITE_TO_OPERATOR_BUTTON_LABEL,
                            BotVariablePropertiesUtil.getVariable(BotVariableType.OPERATOR_LINK)));
            log.debug("Пользователь " + UpdateUtil.getChatId(update) + " выиграл лотерею. Probability=" + probability);
        } else {
            responseSender.sendBotMessage(botMessageService.findByType(BotMessageType.LOSE_LOTTERY), user.getChatId());
            log.trace("Пользователь " + UpdateUtil.getChatId(update) + " проиграл лотерею.");
        }
        user.setLotteryCount(user.getLotteryCount() - 1);
        userService.save(user);
    }

    private boolean getRandomBoolean(float probability) { //0.0 to 99.9
        double randomValue = Math.random() * probability;  //0.0 to 99.9
        return randomValue >= probability;
    }
}
