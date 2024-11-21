package tgb.btc.rce.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.service.IRedisUserStateService;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.IUpdateHandler;
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

    public TelegramUpdateEventListener(IRedisUserStateService redisUserStateService, List<IUpdateHandler> updateHandlers,
                                       List<IStateHandler> stateHandlers) {
        this.redisUserStateService = redisUserStateService;
        log.debug("Загрузка обработчиков апдейтов.");
        this.updateHandlers = new HashMap<>(updateHandlers.size());
        for (IUpdateHandler updateHandler : updateHandlers) {
            UpdateType updateType = updateHandler.getUpdateType();
            if (Objects.isNull(updateType)) {
                throw new RuntimeException("Тип апдета для " + updateHandler.getClass().getName() + " равен null.");
            }
            log.debug("Добавлен обработчик апдейтов типа {}", updateHandler.getUpdateType().name());
            this.updateHandlers.put(updateHandler.getUpdateType(), updateHandler);
        }
        this.stateHandlerMap = new HashMap<>(stateHandlers.size());
        for (IStateHandler stateHandler : stateHandlers) {
            stateHandlerMap.put(stateHandler.getUserState(), stateHandler);
        }
        log.debug("Загружено {} обработчиков апдейтов.", updateHandlers.size());
    }

    @EventListener
    public void update(TelegramUpdateEvent event) {
        log.trace("Получен апдейт: {}", event.getUpdate());
        Update update = event.getUpdate();
        UpdateType updateType = UpdateType.fromUpdate(update);
        if (UpdateType.STATE_UPDATE_TYPES.contains(updateType)) {
            Long chatId = UpdateType.getChatId(update);
            UserState userState = redisUserStateService.get(chatId);
            if (Objects.nonNull(userState)) {
                IStateHandler stateHandler = stateHandlerMap.get(userState);
                if (Objects.nonNull(stateHandler)) {
                    stateHandler.handle(update);
                    return;
                }
            }
        }
        IUpdateHandler updateHandler = updateHandlers.get(updateType);
        if (updateHandler != null) {
            updateHandler.handle(update);
        }
    }
}
