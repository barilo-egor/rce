package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.LotteryWin;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.constants.enums.bot.BotMessageType;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.repository.bot.LotteryWinRepository;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.BotMessageService;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@CommandProcessor(command = Command.LOTTERY)
@Slf4j
public class Lottery extends Processor {

    private BotMessageService botMessageService;

    private LotteryWinRepository lotteryWinRepository;

    @Autowired
    public void setLotteryWinRepository(LotteryWinRepository lotteryWinRepository) {
        this.lotteryWinRepository = lotteryWinRepository;
    }

    @Autowired
    public void setBotMessageService(BotMessageService botMessageService) {
        this.botMessageService = botMessageService;
    }

    @Override
    public void run(Update update) {
        User user = userRepository.findByChatId(UpdateUtil.getChatId(update));
        if(Objects.isNull(user.getLotteryCount()) || user.getLotteryCount() == 0) {
            responseSender.sendMessage(user.getChatId(),
                    MessagePropertiesUtil.getMessage(PropertiesMessage.NO_LOTTERY_ATTEMPTS));
            return;
        }
        processLottery(update, user);
    }

    private void processLottery(Update update, User user) {
        float probability = BotVariablePropertiesUtil.getFloat(VariableType.PROBABILITY);
        if (((double) new Random().nextInt(101) < ((double) probability))) {
            responseSender.sendBotMessage(botMessageService.findByType(BotMessageType.WON_LOTTERY), user.getChatId(),
                    MenuFactory.getLink("Написать оператору",
                            BotVariablePropertiesUtil.getVariable(VariableType.OPERATOR_LINK)));
            Long chatId = UpdateUtil.getChatId(update);
            String username = userRepository.getUsernameByChatId(chatId);
            userRepository.getAdminsChatIds()
                    .forEach(adminChatId -> responseSender.sendMessage(
                            adminChatId, "Пользователь id=" + UpdateUtil.getChatId(update)
                                                 + ", username=" + (StringUtils.isNotEmpty(username) ? username : "скрыт")
                                    + " выиграл лотерею.")
                    );
            log.debug("Пользователь " + UpdateUtil.getChatId(update) + " выиграл лотерею. Probability=" + probability);
            lotteryWinRepository.save(new LotteryWin(user, LocalDateTime.now()));
        } else {
            responseSender.sendBotMessage(botMessageService.findByType(BotMessageType.LOSE_LOTTERY), user.getChatId());
            log.trace("Пользователь " + UpdateUtil.getChatId(update) + " проиграл лотерею.");
        }
        user.setLotteryCount(user.getLotteryCount() - 1);
        userService.save(user);
    }
}
