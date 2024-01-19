package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.repository.bot.UserRepository;
import tgb.btc.library.service.bean.bot.UserService;
import tgb.btc.library.service.process.BannedUserCache;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.SimpleCommand;
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

    private static final boolean IS_LOG_UDPATES = PropertiesPath.FUNCTIONS_PROPERTIES.getBoolean("log.updates", false);
    private final UserService userService;

    private final UserProcessService userProcessService;
    private AntiSpam antiSpam;

    private BannedUserCache bannedUserCache;

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setBannedUserCache(BannedUserCache bannedUserCache) {
        this.bannedUserCache = bannedUserCache;
    }

    @Autowired(required = false)
    public void setAntiSpam(AntiSpam antiSpam) {
        this.antiSpam = antiSpam;
    }

    @Autowired
    public UpdateDispatcher(UserService userService, UserProcessService userProcessService) {
        this.userService = userService;
        this.userProcessService = userProcessService;
    }

    public void dispatch(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (IS_LOG_UDPATES) log.info(chatId.toString());
        if (bannedUserCache.get(chatId)) return;
        runProcessor(getCommand(update, chatId), chatId, update);
    }

    @Async
    public void runProcessor(Command command, Long chatId, Update update) {
        if (!command.isSimple())
            ((Processor) applicationContext
                    .getBean(CommandProcessorLoader.getByCommand(command, userService.getStepByChatId(chatId))))
                    .process(update);
        else SimpleCommand.run(command, update);
    }

    private Command getCommand(Update update, Long chatId) {
        if (Objects.nonNull(antiSpam)) {
            if (isCaptcha(update)) return Command.CAPTCHA;
            antiSpam.saveTime(chatId);
        } else userProcessService.registerIfNotExists(update);
        if (isOffed(chatId)) return Command.BOT_OFFED;
        if (CommandUtil.isStartCommand(update)) return Command.START;
        Command command;
        if (userService.isDefaultStep(chatId)) command = Command.fromUpdate(update);
        else command = Command.valueOf(userRepository.getCommandByChatId(chatId));
        if (Objects.isNull(command) || !hasAccess(command, chatId)) return Command.START;
        else return command;
    }

    private boolean isCaptcha(Update update) {
        return !userProcessService.registerIfNotExists(update) || antiSpam.isSpamUser(UpdateUtil.getChatId(update));
    }

    private boolean isOffed(Long chatId) {
        return !isOn() && BooleanUtils.isNotTrue(userService.isAdminByChatId(chatId));
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
