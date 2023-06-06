package tgb.btc.lib.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.bean.LotteryWin;
import tgb.btc.lib.bean.User;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.BotMessageType;
import tgb.btc.lib.enums.BotVariableType;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.enums.PropertiesMessage;
import tgb.btc.lib.repository.LotteryWinRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.impl.BotMessageService;
import tgb.btc.lib.util.BotVariablePropertiesUtil;
import tgb.btc.lib.util.MenuFactory;
import tgb.btc.lib.util.MessagePropertiesUtil;
import tgb.btc.lib.util.UpdateUtil;

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
        if (((double) new Random().nextInt(101) < ((double) probability))) {
            responseSender.sendBotMessage(botMessageService.findByType(BotMessageType.WON_LOTTERY), user.getChatId(),
                    MenuFactory.getLink(BotStringConstants.WRITE_TO_OPERATOR_BUTTON_LABEL,
                            BotVariablePropertiesUtil.getVariable(BotVariableType.OPERATOR_LINK)));
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
