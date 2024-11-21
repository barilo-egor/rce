package tgb.btc.rce.service.handler.impl.message;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.MessageType;
import tgb.btc.rce.service.IRedisUserStateService;
import tgb.btc.rce.service.handler.message.IMessageHandler;
import tgb.btc.rce.service.handler.message.contact.IStateContactMessageHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ContactMessageHandler implements IMessageHandler {

    private final IRedisUserStateService redisUserStateService;

    private final Map<UserState, IStateContactMessageHandler> stateContactMessageHandlerMap = new HashMap<>();

    public ContactMessageHandler(List<IStateContactMessageHandler> stateContactMessageHandlers,
                                 IRedisUserStateService redisUserStateService) {
        this.redisUserStateService = redisUserStateService;
        for (IStateContactMessageHandler stateContactMessageHandler : stateContactMessageHandlers) {
            stateContactMessageHandlerMap.put(stateContactMessageHandler.getUserState(), stateContactMessageHandler);
        }
    }

    @Override
    public void handleMessage(Message message) {
        UserState userState = redisUserStateService.get(message.getChatId());
        if (Objects.isNull(userState)) {
            return;
        }
        IStateContactMessageHandler handler = stateContactMessageHandlerMap.get(userState);
        if (Objects.isNull(handler)) {
            return;
        }
        handler.handle(message);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.CONTACT;
    }
}
