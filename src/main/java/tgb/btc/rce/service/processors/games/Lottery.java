package tgb.btc.rce.service.processors.games;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.LotteryWin;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.constants.enums.bot.BotMessageType;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.interfaces.service.bean.bot.IBotMessageService;
import tgb.btc.library.interfaces.service.bean.bot.ILotteryWinService;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.INotifyService;
import tgb.btc.rce.service.Processor;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

@CommandProcessor(command = Command.LOTTERY)
@Slf4j
public class Lottery extends Processor {

    private IBotMessageService botMessageService;

    private ILotteryWinService lotteryWinService;

    private INotifyService notifyService;

    private VariablePropertiesReader variablePropertiesReader;

    @Autowired
    public void setNotifyService(INotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @Autowired
    public void setVariablePropertiesReader(VariablePropertiesReader variablePropertiesReader) {
        this.variablePropertiesReader = variablePropertiesReader;
    }

    @Autowired
    public void setLotteryWinService(ILotteryWinService lotteryWinService) {
        this.lotteryWinService = lotteryWinService;
    }

    @Autowired
    public void setBotMessageService(IBotMessageService botMessageService) {
        this.botMessageService = botMessageService;
    }

    @Override
    public void run(Update update) {
        User user = readUserService.findByChatId(updateService.getChatId(update));
        if(Objects.isNull(user.getLotteryCount()) || user.getLotteryCount() == 0) {
            responseSender.sendMessage(user.getChatId(),
                    messagePropertiesService.getMessage(PropertiesMessage.NO_LOTTERY_ATTEMPTS));
            return;
        }
        processLottery(update, user);
    }

    private void processLottery(Update update, User user) {
        float probability = variablePropertiesReader.getFloat(VariableType.PROBABILITY);
        if (((double) new Random().nextInt(101) < ((double) probability))) {
            responseSender.sendBotMessage(botMessageService.findByTypeNullSafe(BotMessageType.WON_LOTTERY), user.getChatId(),
                    keyboardBuildService.getLink("Написать оператору",
                            variablePropertiesReader.getVariable(VariableType.OPERATOR_LINK)));
            String username = user.getUsername();
            notifyService.notifyMessage("Пользователь id=" + updateService.getChatId(update)
                    + ", username=" + (StringUtils.isNotEmpty(username) ? username : "скрыт")
                    + " выиграл лотерею.", Set.of(UserRole.OPERATOR, UserRole.ADMIN));
            log.debug("Пользователь " + updateService.getChatId(update) + " выиграл лотерею. Probability=" + probability);
            lotteryWinService.save(new LotteryWin(user, LocalDateTime.now()));
        } else {
            responseSender.sendBotMessage(botMessageService.findByTypeNullSafe(BotMessageType.LOSE_LOTTERY), user.getChatId());
            log.trace("Пользователь " + updateService.getChatId(update) + " проиграл лотерею.");
        }
        user.setLotteryCount(user.getLotteryCount() - 1);
        modifyUserService.save(user);
    }
}
