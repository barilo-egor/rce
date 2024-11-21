package tgb.btc.rce.service.impl.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.service.bean.common.bot.IUserCommonService;
import tgb.btc.library.service.process.BannedUserCache;
import tgb.btc.library.service.properties.ConfigPropertiesReader;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.service.IRedisUserStateService;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.captcha.IAntiSpam;
import tgb.btc.rce.service.process.IUserProcessService;
import tgb.btc.rce.service.util.ICommandProcessorLoader;
import tgb.btc.rce.service.util.ICommandService;
import tgb.btc.rce.service.util.IUpdateDispatcher;
import tgb.btc.rce.vo.TelegramUpdateEvent;

import java.util.Objects;

@Service
@Slf4j
public class UpdateDispatcher implements IUpdateDispatcher {

    private static boolean IS_ON = true;

    private IReadUserService readUserService;

    private IUserProcessService userProcessService;

    private IAntiSpam antiSpam;

    private BannedUserCache bannedUserCache;

    private IUserCommonService userCommonService;

    private ICommandProcessorLoader commandProcessorLoader;

    private ICommandService commandService;

    private IUpdateService updateService;

    private ApplicationEventPublisher eventPublisher;

    private ConfigPropertiesReader configPropertiesReader;

    private IRedisUserStateService redisUserStateService;

    @Autowired
    public void setRedisUserStateService(IRedisUserStateService redisUserStateService) {
        this.redisUserStateService = redisUserStateService;
    }

    @Autowired
    public void setConfigPropertiesReader(ConfigPropertiesReader configPropertiesReader) {
        this.configPropertiesReader = configPropertiesReader;
    }

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Autowired
    public void setUpdateService(IUpdateService updateService) {
        this.updateService = updateService;
    }

    @Autowired
    public void setCommandService(ICommandService commandService) {
        this.commandService = commandService;
    }

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
    public void setAntiSpam(IAntiSpam antiSpam) {
        this.antiSpam = antiSpam;
    }

    @Autowired
    public void setUserProcessService(IUserProcessService userProcessService) {
        this.userProcessService = userProcessService;
    }

    @PostConstruct
    public void setIsOn() {
        Boolean turnOffOnStart = configPropertiesReader.getBoolean("turn.off.on.start");
        if (BooleanUtils.isTrue(turnOffOnStart)) setIsOn(false);
    }

    public void dispatch(Update update) {
        Long chatId = updateService.getChatId(update);
        if (bannedUserCache.get(chatId)) return;
        UserState userState = redisUserStateService.get(chatId);
        if (Objects.isNull(userState)) {
            Command command = getCommand(update, chatId);
            if (!Command.NEW_HANDLE.contains(command)) {
                runProcessor(command, chatId, update);
                return;
            }
        }
        eventPublisher.publishEvent(new TelegramUpdateEvent(this, update));
    }

    public void runProcessor(Command command, Long chatId, Update update) {
        Processor processor = commandProcessorLoader.getByCommand(command, readUserService.getStepByChatId(chatId));
        if (Objects.nonNull(processor)) {
            processor.process(update);
            return;
        }
        eventPublisher.publishEvent(new TelegramUpdateEvent(this, update));
    }

    private Command getCommand(Update update, Long chatId) {
        if (Objects.nonNull(antiSpam) && !antiSpam.isVerifiedUser(chatId)) {
            if (isCaptcha(update)) return Command.CAPTCHA;
            antiSpam.saveTime(chatId);
        } else userProcessService.registerIfNotExists(update);
        if (isOffed(chatId) && !commandService.isSubmitCommand(update)) return Command.BOT_OFFED;
        if (commandService.isStartCommand(update)) return Command.START;
        Command command;
        if (userCommonService.isDefaultStep(chatId)) command = commandService.fromUpdate(update);
        else command = Command.valueOf(readUserService.getCommandByChatId(chatId));
        if (Objects.isNull(command) || !command.hasAccess(readUserService.getUserRoleByChatId(chatId))) return Command.START;
        else return command;
    }

    private boolean isCaptcha(Update update) {
        return !userProcessService.registerIfNotExists(update) || antiSpam.isSpamUser(updateService.getChatId(update));
    }

    private boolean isOffed(Long chatId) {
        return !isOn() && UserRole.USER.equals(readUserService.getUserRoleByChatId(chatId));
    }

    public boolean isOn() {
        return IS_ON;
    }

    public static void setIsOn(boolean isOn) {
        IS_ON = isOn;
    }
}
