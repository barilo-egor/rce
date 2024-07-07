package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.service.bean.common.bot.IUserCommonService;
import tgb.btc.library.service.process.BannedUserCache;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.SimpleCommand;
import tgb.btc.rce.service.AntiSpam;
import tgb.btc.rce.service.ICommandProcessorLoader;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.service.impl.process.UserProcessService;
import tgb.btc.rce.util.CommandUtil;
import tgb.btc.rce.util.UpdateUtil;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Service
@Slf4j
public class UpdateDispatcher implements IUpdateDispatcher {

    public static ApplicationContext applicationContext;

    private static boolean IS_ON = true;

    private static final boolean IS_LOG_UDPATES = PropertiesPath.FUNCTIONS_PROPERTIES.getBoolean("log.updates", false);

    private IReadUserService readUserService;

    private UserProcessService userProcessService;

    private AntiSpam antiSpam;

    private BannedUserCache bannedUserCache;

    private IUserCommonService userCommonService;

    private ICommandProcessorLoader commandProcessorLoader;

    @Autowired
    public void setCommandProcessorLoader(@Lazy ICommandProcessorLoader commandProcessorLoader) {
        this.commandProcessorLoader = commandProcessorLoader;
    }

    @Autowired
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
    }

    @Autowired
    public void setUserCommonService(IUserCommonService userCommonService) {
        this.userCommonService = userCommonService;
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
    public void setUserProcessService(UserProcessService userProcessService) {
        this.userProcessService = userProcessService;
    }

    @PostConstruct
    public void setIsOn() {
        Boolean turnOffOnStart = PropertiesPath.CONFIG_PROPERTIES.getBoolean("turn.off.on.start");
        if (BooleanUtils.isTrue(turnOffOnStart)) setIsOn(false);
    }

    public void dispatch(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (IS_LOG_UDPATES) log.info(chatId.toString());
        if (bannedUserCache.get(chatId)) return;
        runProcessor(getCommand(update, chatId), chatId, update);
    }

    public void runProcessor(Command command, Long chatId, Update update) {
        if (!command.isSimple())
            commandProcessorLoader.getByCommand(command, readUserService.getStepByChatId(chatId)).process(update);
        else SimpleCommand.run(command, update);
    }

    private Command getCommand(Update update, Long chatId) {
        if (Objects.nonNull(antiSpam) && !antiSpam.isVerifiedUser(chatId)) {
            if (isCaptcha(update)) return Command.CAPTCHA;
            antiSpam.saveTime(chatId);
        } else userProcessService.registerIfNotExists(update);
        if (isOffed(chatId) && !CommandUtil.isSubmitCommand(update)) return Command.BOT_OFFED;
        if (CommandUtil.isStartCommand(update)) return Command.START;
        Command command;
        if (userCommonService.isDefaultStep(chatId)) command = Command.fromUpdate(update);
        else command = Command.valueOf(readUserService.getCommandByChatId(chatId));
        if (Objects.isNull(command) || !command.hasAccess(readUserService.getUserRoleByChatId(chatId))) return Command.START;
        else return command;
    }

    private boolean isCaptcha(Update update) {
        return !userProcessService.registerIfNotExists(update) || antiSpam.isSpamUser(UpdateUtil.getChatId(update));
    }

    private boolean isOffed(Long chatId) {
        return !isOn() && UserRole.USER.equals(readUserService.getUserRoleByChatId(chatId));
    }

    public static boolean isOn() {
        return IS_ON;
    }

    public static void setIsOn(boolean isOn) {
        IS_ON = isOn;
    }
}
