package tgb.btc.rce.service.impl.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.service.process.BannedUserCache;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.processors.deal.DealProcessor;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.IUpdateDispatcher;
import tgb.btc.rce.vo.TelegramUpdateEvent;

import java.util.Objects;

@Service
@Slf4j
public class UpdateDispatcher implements IUpdateDispatcher {

    private IReadUserService readUserService;

    private BannedUserCache bannedUserCache;

    private IUpdateService updateService;

    private ApplicationEventPublisher eventPublisher;

    private IRedisUserStateService redisUserStateService;

    private DealProcessor dealProcessor;

    @Autowired
    public void setDealProcessor(DealProcessor dealProcessor) {
        this.dealProcessor = dealProcessor;
    }

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
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
    }

    @Autowired
    public void setBannedUserCache(BannedUserCache bannedUserCache) {
        this.bannedUserCache = bannedUserCache;
    }

    public void dispatch(Update update) {
        Long chatId = updateService.getChatId(update);
        if (bannedUserCache.get(chatId)) return;
        UserState userState = redisUserStateService.get(chatId);
        if (Objects.isNull(userState)) {
            String userCommand = readUserService.getCommandByChatId(chatId);
            if (userCommand.equals(Command.DEAL.name())) {
                dealProcessor.process(update);
                return;
            }
        }
        eventPublisher.publishEvent(new TelegramUpdateEvent(this, update));
    }
}
