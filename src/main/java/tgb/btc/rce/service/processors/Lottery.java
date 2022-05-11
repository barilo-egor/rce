package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.BotMessageType;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.BotMessageService;
import tgb.btc.rce.service.impl.BotVariableService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.UpdateUtil;

import java.util.Objects;

@CommandProcessor(command = Command.LOTTERY)
@Slf4j
public class Lottery extends Processor {

    private static final String WRITE_TO_OPERATOR_BUTTON_LABEL = "Написать оператору";

    private final BotMessageService botMessageService;
    private final UserService userService;
    private final BotVariableService botVariableService;

    @Autowired
    public Lottery(IResponseSender responseSender, BotMessageService botMessageService, UserService userService,
                   BotVariableService botVariableService) {
        super(responseSender);
        this.botMessageService = botMessageService;
        this.userService = userService;
        this.botVariableService = botVariableService;
    }

    @Override
    public void run(Update update) {
        User user = userService.findByChatId(update);
        if(Objects.isNull(user.getLotteryCount()) || user.getLotteryCount() == 0) {
            responseSender.sendMessage(user.getChatId(), "У тебя нету попыток на игру в лотерею." +
                    "Чтобы получить попытку соверши сделку\uD83D\uDC47");
            return;
        }
        processLottery(update, user);
    }

    private void processLottery(Update update, User user) {
        if (getRandomBoolean(Float.parseFloat(botVariableService.findByType(BotVariableType.PROBABILITY).getValue()))) {
            responseSender.sendBotMessage(botMessageService.findByType(BotMessageType.WON_LOTTERY), user.getChatId(),
                    MenuFactory.getLink(WRITE_TO_OPERATOR_BUTTON_LABEL,
                            botVariableService.findByType(BotVariableType.OPERATOR_LINK).getValue()));
            log.debug("Пользователь " + UpdateUtil.getChatId(update) + " выиграл лотерею.");
        }
        else {
            responseSender.sendBotMessage(botMessageService.findByType(BotMessageType.LOSE_LOTTERY), user.getChatId());
            log.debug("Пользователь " + UpdateUtil.getChatId(update) + " проиграл лотерею.");
        }
        user.setLotteryCount(user.getLotteryCount() - 1);
        userService.save(user);
    }

    private boolean getRandomBoolean(float probability) {
        double randomValue = Math.random() * probability;  //0.0 to 99.9
        return randomValue >= probability;
    }
}
