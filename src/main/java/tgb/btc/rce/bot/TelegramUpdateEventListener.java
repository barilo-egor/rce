package tgb.btc.rce.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.service.process.BannedUserCache;
import tgb.btc.rce.enums.UpdateFilterType;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.exception.HandlerTypeNotFoundException;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.captcha.IAntiSpamService;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.IUpdateFilter;
import tgb.btc.rce.service.handler.IUpdateHandler;
import tgb.btc.rce.service.handler.util.IUpdateFilterService;
import tgb.btc.rce.service.processors.support.MessagesService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.vo.TelegramUpdateEvent;

import java.util.EnumMap;
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

    private final IResponseSender responseSender;

    public TelegramUpdateEventListener(IRedisUserStateService redisUserStateService, List<IUpdateHandler> updateHandlers,
                                       List<IStateHandler> stateHandlers, MessagesService messagesService,
                                       List<IUpdateFilter> updateFilters, IUpdateFilterService updateFilterService,
                                       IAntiSpamService antiSpamService, BannedUserCache bannedUserCache,
                                       IResponseSender responseSender) {
        this.redisUserStateService = redisUserStateService;
        this.messagesService = messagesService;
        this.updateFilterService = updateFilterService;
        this.antiSpamService = antiSpamService;
        this.bannedUserCache = bannedUserCache;
        this.responseSender = responseSender;
        log.debug("Загрузка обработчиков апдейтов.");
        this.updateHandlers = new EnumMap<>(UpdateType.class);
        for (IUpdateHandler updateHandler : updateHandlers) {
            UpdateType updateType = updateHandler.getUpdateType();
            if (Objects.isNull(updateType)) {
                throw new HandlerTypeNotFoundException("UpdateType null для " + updateHandler.getClass().getName());
            }
            log.debug("Добавлен обработчик апдейтов типа {}", updateHandler.getUpdateType().name());
            this.updateHandlers.put(updateHandler.getUpdateType(), updateHandler);
        }
        this.stateHandlerMap = new EnumMap<>(UserState.class);
        for (IStateHandler stateHandler : stateHandlers) {
            UserState userState = stateHandler.getUserState();
            if (Objects.isNull(userState)) {
                throw new HandlerTypeNotFoundException("UserState null для " + stateHandler.getClass().getName());
            }
            stateHandlerMap.put(stateHandler.getUserState(), stateHandler);
        }
        this.updateFilterMap = new EnumMap<>(UpdateFilterType.class);
        for (IUpdateFilter updateFilter : updateFilters) {
            UpdateFilterType updateFilterType = updateFilter.getType();
            if (Objects.isNull(updateFilterType)) {
                throw new HandlerTypeNotFoundException("UpdateFilterType null для " + updateFilter.getClass().getName());
            }
            updateFilterMap.put(updateFilter.getType(), updateFilter);
        }
        log.debug("Загружено {} обработчиков апдейтов.", updateHandlers.size());
    }

    @EventListener
    @Async
    public void update(TelegramUpdateEvent event) {
        Update update = event.getUpdate();
        log.trace("Получен апдейт: {}", event.getUpdate());
        Long chatId = UpdateType.getChatId(update);
        try {
            if (bannedUserCache.get(chatId)) return;
            if (antiSpamService.isSpam(chatId)) return;
            if (handleFilter(update)) return;
            UpdateType updateType = UpdateType.fromUpdate(update);
            if (handleState(update, updateType, chatId)) return;
            if (!handle(update, updateType)) {
                Chat chat = UpdateType.getChat(update);
                if (Objects.nonNull(chat) && chat.isUserChat()) {
                    messagesService.sendNoHandler(UpdateType.getChatId(update));
                }
            }
        } catch (NumberFormatException e) {
            responseSender.sendMessage(chatId, "Неверный формат.");
        } catch (Exception e) {
            Long time = System.currentTimeMillis();
            log.error("{} Необработанная ошибка.", time, e);
            responseSender.sendMessage(chatId,
                    "Произошла ошибка." + System.lineSeparator() + time + System.lineSeparator()
                            + "Введите /start для выхода в главное меню."
            );
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
