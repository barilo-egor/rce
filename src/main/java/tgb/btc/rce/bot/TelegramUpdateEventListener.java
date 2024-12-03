package tgb.btc.rce.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.service.process.BannedUserCache;
import tgb.btc.rce.enums.UpdateFilterType;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.exception.HandlerTypeNotFoundException;
import tgb.btc.rce.service.captcha.IAntiSpamService;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.IUpdateFilter;
import tgb.btc.rce.service.handler.IUpdateHandler;
import tgb.btc.rce.service.handler.util.IUpdateFilterService;
import tgb.btc.rce.service.processors.support.MessagesService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.vo.TelegramUpdateEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class TelegramUpdateEventListener {

    private final IRedisUserStateService redisUserStateService;

    private final Map<UpdateType, IUpdateHandler> updateHandlers;

    private final Map<UserState, IStateHandler> stateHandlerMap;

    private final Map<UpdateFilterType, IUpdateFilter> updateFilterMap;

    private final MessagesService messagesService;

    private final IUpdateFilterService updateFilterService;

    private final IAntiSpamService antiSpamService;

    private final BannedUserCache bannedUserCache;

    public TelegramUpdateEventListener(IRedisUserStateService redisUserStateService, List<IUpdateHandler> updateHandlers,
                                       List<IStateHandler> stateHandlers, MessagesService messagesService,
                                       List<IUpdateFilter> updateFilters, IUpdateFilterService updateFilterService,
                                       IAntiSpamService antiSpamService, BannedUserCache bannedUserCache) {
        this.redisUserStateService = redisUserStateService;
        this.messagesService = messagesService;
        this.updateFilterService = updateFilterService;
        this.antiSpamService = antiSpamService;
        this.bannedUserCache = bannedUserCache;
        log.debug("Загрузка обработчиков апдейтов.");
        this.updateHandlers = new HashMap<>(updateHandlers.size());
        for (IUpdateHandler updateHandler : updateHandlers) {
            UpdateType updateType = updateHandler.getUpdateType();
            if (Objects.isNull(updateType)) {
                throw new HandlerTypeNotFoundException("UpdateType для " + updateHandler.getClass().getName() + " равен null.");
            }
            log.debug("Добавлен обработчик апдейтов типа {}", updateHandler.getUpdateType().name());
            this.updateHandlers.put(updateHandler.getUpdateType(), updateHandler);
        }
        this.stateHandlerMap = new HashMap<>(stateHandlers.size());
        for (IStateHandler stateHandler : stateHandlers) {
            UserState userState = stateHandler.getUserState();
            if (Objects.isNull(userState)) {
                throw new HandlerTypeNotFoundException("UserState для " + stateHandler.getClass().getName() + " равен null.");
            }
            stateHandlerMap.put(stateHandler.getUserState(), stateHandler);
        }
        this.updateFilterMap = new HashMap<>(updateFilters.size());
        for (IUpdateFilter updateFilter : updateFilters) {
            UpdateFilterType updateFilterType = updateFilter.getType();
            if (Objects.isNull(updateFilterType)) {
                throw new HandlerTypeNotFoundException("UpdateFilterType для " + updateFilter.getClass().getName() + " равен null.");
            }
            updateFilterMap.put(updateFilter.getType(), updateFilter);
        }
        log.debug("Загружено {} обработчиков апдейтов.", updateHandlers.size());
    }

    @EventListener
    public void update(TelegramUpdateEvent event) {
        log.trace("Получен апдейт: {}", event.getUpdate());
        Update update = event.getUpdate();
        Long chatId = UpdateType.getChatId(update);
        if (bannedUserCache.get(chatId)) return;
        if (antiSpamService.isSpam(chatId)) return;
        if (handleFilter(update)) return;
        UpdateType updateType = UpdateType.fromUpdate(update);
        if (handleState(update, updateType, chatId)) return;
        if (!handle(update, updateType)) {
            messagesService.sendNoHandler(UpdateType.getChatId(update));
        }
    }

    private boolean handle(Update update, UpdateType updateType) {
        IUpdateHandler updateHandler = updateHandlers.get(updateType);
        if (updateHandler != null) {
            return updateHandler.handle(update);
        }
        return false;
    }

    private boolean handleState(Update update, UpdateType updateType, Long chatId) {
        if (UpdateType.STATE_UPDATE_TYPES.contains(updateType)) {
            UserState userState = redisUserStateService.get(chatId);
            if (Objects.nonNull(userState)) {
                IStateHandler stateHandler = stateHandlerMap.get(userState);
                if (Objects.nonNull(stateHandler)) {
                    stateHandler.handle(update);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean handleFilter(Update update) {
        UpdateFilterType updateFilterType = updateFilterService.getType(update);
        if (Objects.nonNull(updateFilterType)) {
            IUpdateFilter updateFilter = updateFilterMap.get(updateFilterType);
            if (Objects.nonNull(updateFilter)) {
                updateFilter.handle(update);
                return true;
            }
        }
        return false;
    }
}
