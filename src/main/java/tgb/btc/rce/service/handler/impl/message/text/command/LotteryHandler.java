package tgb.btc.rce.service.handler.impl.message.text.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.LotteryWin;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.interfaces.enums.MessageImage;
import tgb.btc.library.interfaces.service.bean.bot.ILotteryWinService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IMessageImageResponseSender;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.INotifyService;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.IMessagePropertiesService;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

@Service
@Slf4j
public class LotteryHandler implements ITextCommandHandler {

    private final ILotteryWinService lotteryWinService;

    private final INotifyService notifyService;

    private final VariablePropertiesReader variablePropertiesReader;

    private final IReadUserService readUserService;

    private final IResponseSender responseSender;

    private final IMessagePropertiesService messagePropertiesService;

    private final IKeyboardBuildService keyboardBuildService;

    private final IModifyUserService modifyUserService;

    private final IMessageImageResponseSender messageImageResponseSender;

    private final Random random = new Random();

    public LotteryHandler(ILotteryWinService lotteryWinService,
                          INotifyService notifyService, VariablePropertiesReader variablePropertiesReader,
                          IReadUserService readUserService, IResponseSender responseSender,
                          IMessagePropertiesService messagePropertiesService, IKeyboardBuildService keyboardBuildService,
                          IModifyUserService modifyUserService, IMessageImageResponseSender messageImageResponseSender) {
        this.lotteryWinService = lotteryWinService;
        this.notifyService = notifyService;
        this.variablePropertiesReader = variablePropertiesReader;
        this.readUserService = readUserService;
        this.responseSender = responseSender;
        this.messagePropertiesService = messagePropertiesService;
        this.keyboardBuildService = keyboardBuildService;
        this.modifyUserService = modifyUserService;
        this.messageImageResponseSender = messageImageResponseSender;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        User user = readUserService.findByChatId(chatId);
        if(Objects.isNull(user.getLotteryCount()) || user.getLotteryCount() == 0) {
            responseSender.sendMessage(user.getChatId(),
                    messagePropertiesService.getMessage(PropertiesMessage.NO_LOTTERY_ATTEMPTS));
            return;
        }
        processLottery(chatId, user);
    }

    private void processLottery(Long chatId, User user) {
        float probability = variablePropertiesReader.getFloat(VariableType.PROBABILITY);
        if (((double) random.nextInt(101) < ((double) probability))) {
            messageImageResponseSender.sendMessage(MessageImage.WON_LOTTERY, user.getChatId(),
                    keyboardBuildService.getLink("Написать оператору",
                            variablePropertiesReader.getVariable(VariableType.OPERATOR_LINK)));
            String username = user.getUsername();
            notifyService.notifyMessage("Пользователь chatId=" + chatId
                    + ", username=" + (StringUtils.isNotEmpty(username) ? username : "скрыт")
                    + " выиграл лотерею.", Set.of(UserRole.OPERATOR, UserRole.ADMIN));
            log.debug("Пользователь {} выиграл лотерею. Probability={}", chatId, probability);
            lotteryWinService.save(new LotteryWin(user, LocalDateTime.now()));
        } else {
            messageImageResponseSender.sendMessage(MessageImage.LOSE_LOTTERY, user.getChatId());
            log.trace("Пользователь {} проиграл лотерею.", chatId);
        }
        user.setLotteryCount(user.getLotteryCount() - 1);
        modifyUserService.save(user);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.LOTTERY;
    }
}
