package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.LotteryWin;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.BotMessageType;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.repository.LotteryWinRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.BotMessageService;
import tgb.btc.rce.service.impl.UserService;
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

    private final BotMessageService botMessageService;

    private UserRepository userRepository;

    private LotteryWinRepository lotteryWinRepository;

    @Autowired
    public void setLotteryWinRepository(LotteryWinRepository lotteryWinRepository) {
        this.lotteryWinRepository = lotteryWinRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
