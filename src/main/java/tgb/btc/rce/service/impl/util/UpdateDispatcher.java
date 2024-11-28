package tgb.btc.rce.service.impl.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.service.bean.common.bot.IUserCommonService;
import tgb.btc.library.service.process.BannedUserCache;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.process.IUserProcessService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICommandProcessorLoader;
import tgb.btc.rce.service.util.ICommandService;
import tgb.btc.rce.service.util.IUpdateDispatcher;
import tgb.btc.rce.vo.TelegramUpdateEvent;

import java.util.Objects;

@Service
@Slf4j
public class UpdateDispatcher implements IUpdateDispatcher {

    private IReadUserService readUserService;

    private IUserProcessService userProcessService;
    private BannedUserCache bannedUserCache;

    private IUserCommonService userCommonService;

    private ICommandProcessorLoader commandProcessorLoader;

    private ICommandService commandService;

    private IUpdateService updateService;

    private ApplicationEventPublisher eventPublisher;

    private IRedisUserStateService redisUserStateService;

    @Autowired
    public void setRedisUserStateService(IRedisUserStateService redisUserStateService) {
        this.redisUserStateService = redisUserStateService;
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

    @Autowired
    public void setUserProcessService(IUserProcessService userProcessService) {
        this.userProcessService = userProcessService;
    }

    public void dispatch(Update update) {
        Long chatId = updateService.getChatId(update);
        if (bannedUserCache.get(chatId)) return;
        UserState userState = redisUserStateService.get(chatId);
        if (Objects.isNull(userState)) {
            Command command = getCommand(update, chatId);
            runProcessor(command, chatId, update);
            return;
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
        if (!readUserService.existsByChatId(chatId)) return null;
        if (commandService.isStartCommand(update)) return null;
        Command command;
        if (userCommonService.isDefaultStep(chatId)) command = commandService.fromUpdate(update);
        else command = Command.valueOf(readUserService.getCommandByChatId(chatId));
        if (Objects.isNull(command) || !command.hasAccess(readUserService.getUserRoleByChatId(chatId))) return null;
        else return command;
    }
}
