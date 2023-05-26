package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.AntiSpam;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.CommandProcessorLoader;
import tgb.btc.rce.util.CommandUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.util.Objects;

@Service
@Slf4j
public class UpdateDispatcher implements IUpdateDispatcher {

    public static ApplicationContext applicationContext;
    private static boolean IS_ON = false; // TODO

    private final UserService userService;

    private BannedUserCache bannedUserCache;

    private AntiSpam antiSpam;

    @Autowired
    public void setBannedUserCache(BannedUserCache bannedUserCache) {
        this.bannedUserCache = bannedUserCache;
    }

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
        if (BooleanUtils.isTrue(bannedUserCache.get(chatId))) return;
        Command command = getCommand(update);
        int step = userService.getStepByChatId(chatId);
        ((Processor) applicationContext.getBean(CommandProcessorLoader.getByCommand(command, step))).process(update);
    }

    private Command getCommand(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        boolean isUserExists = userService.existByChatId(chatId);
        if (!isUserExists || antiSpam.isSpamUser(chatId)) {
            if (!isUserExists) {
                userService.register(update);
                antiSpam.addUser(chatId);
            }
            return Command.CAPTCHA;
        }
        Command command;
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
