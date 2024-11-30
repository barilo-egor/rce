package tgb.btc.rce.service.impl.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.service.process.BannedUserCache;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.util.IUpdateDispatcher;
import tgb.btc.rce.vo.TelegramUpdateEvent;

@Service
@Slf4j
public class UpdateDispatcher implements IUpdateDispatcher {

    private BannedUserCache bannedUserCache;

    private IUpdateService updateService;

    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Autowired
    public void setUpdateService(IUpdateService updateService) {
        this.updateService = updateService;
    }

    @Autowired
    public void setBannedUserCache(BannedUserCache bannedUserCache) {
        this.bannedUserCache = bannedUserCache;
    }

    public void dispatch(Update update) {
        Long chatId = updateService.getChatId(update);
        if (bannedUserCache.get(chatId)) return;
        eventPublisher.publishEvent(new TelegramUpdateEvent(this, update));
    }
}
