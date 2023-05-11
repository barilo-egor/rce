package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.AntiSpam;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.*;

import java.util.Objects;

@Service
@Slf4j
public class UpdateDispatcher implements IUpdateDispatcher {

    public static ApplicationContext applicationContext;
    private static boolean IS_ON = false; // TODO

    private final UserService userService;

    private AntiSpam antiSpam;

    @Autowired
    public void setAntiSpam(AntiSpam antiSpam) {
        this.antiSpam = antiSpam;
    }

    @Autowired
    public UpdateDispatcher(UserService userService) {
        this.userService = userService;
    }

    public void dispatch(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        antiSpam.saveTime(chatId);
        if (BooleanUtils.isTrue(userService.getIsBannedByChatId(chatId))) return;
        if (!userService.existByChatId(chatId)) userService.register(update);
        Command command = getCommand(update);
        int step = userService.getStepByChatId(chatId);
        ((Processor) applicationContext.getBean(CommandProcessorLoader.getByCommand(command, step))).process(update);
    }

    private Command getCommand(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Command command;
        if (antiSpam.isSpamUser(chatId)) {
            return Command.CAPTCHA;
        }
        if (!isOn() && !userService.isAdminByChatId(chatId)) return Command.BOT_OFFED;
        if (userService.getStepByChatId(chatId).equals(User.DEFAULT_STEP) || CommandUtil.isStartCommand(update))
            command = Command.fromUpdate(update);
        else command = userService.getCommandByChatId(chatId);
        if (Objects.isNull(command)) return Command.START;
        if (!hasAccess(command, chatId)) return Command.START;
        else return command;
    }

    private boolean hasAccess(Command command, Long chatId) {
        if (!command.isAdmin()) return true;
        else return userService.isAdminByChatId(chatId);
    }

    public static boolean isOn() {
        return IS_ON;
    }

    public static void setIsOn(boolean isOn) {
        IS_ON = isOn;
    }
}
