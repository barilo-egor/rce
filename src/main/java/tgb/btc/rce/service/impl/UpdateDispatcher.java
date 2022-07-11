package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.*;

@Service
@Slf4j
public class UpdateDispatcher implements IUpdateDispatcher {

    public static ApplicationContext applicationContext;
    private static boolean IS_ON = false; // TODO

    private final UserService userService;

    @Autowired
    public UpdateDispatcher(UserService userService) {
        this.userService = userService;
    }

    public void dispatch(Update update) {
        if (update.hasChannelPost()) {
            log.info("Сообщение из канала: " + update.getChannelPost().getChatId());
        }
        Long chatId = UpdateUtil.getChatId(update);
        if (update.hasInlineQuery() && userService.getStepByChatId(chatId).equals(User.DEFAULT_STEP)) {
            dispatchByInlineQuery(update);
            return;
        }
        if (!userService.existByChatId(chatId)) userService.register(update);
        if (userService.getIsBannedByChatId(chatId)) return;
        Command command;
        try {
            if (!isOn() && !userService.isAdminByChatId(chatId)) {
                command = Command.BOT_OFFED;
            } else {
                command = getCommand(update);
            }
        } catch (BaseException e) {
            command = Command.START;
        }
        ((Processor) applicationContext.getBean(CommandProcessorLoader.getByCommand(command))).run(update);
    }

    public void dispatchByInlineQuery(Update update) {
        String query = update.getInlineQuery().getQuery();
        if (query.startsWith(BotPropertiesUtil.getProperty("bot.link"))) {
            ((Processor) applicationContext.getBean(CommandProcessorLoader.getByCommand(Command.SEND_LINK))).run(update);
        }
    }

    private Command getCommand(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (update.hasChannelPost()) {
            return Command.CHANNEL_POST;
        }
        Command command = userService.getStepByChatId(chatId).equals(User.DEFAULT_STEP) || CommandUtil.isStartCommand(update) ?
                Command.fromUpdate(update) : userService.getCommandByChatId(chatId);
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
