package tgb.btc.rce.service.handler.impl.message;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.MessageType;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.enums.update.TextMessageType;
import tgb.btc.rce.service.IRedisUserStateService;
import tgb.btc.rce.service.handler.message.IMessageHandler;
import tgb.btc.rce.service.handler.message.text.IStateTextMessageHandler;
import tgb.btc.rce.service.handler.message.text.ITextMessageHandler;
import tgb.btc.rce.service.handler.util.IStartService;
import tgb.btc.rce.service.handler.util.ITextMessageTypeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class TextMessageHandler implements IMessageHandler {

    private final ITextMessageTypeService textMessageTypeService;

    private final IRedisUserStateService redisUserStateService;

    private final IStartService startService;

    private final Map<TextMessageType, ITextMessageHandler> textMessageHandlersMap = new HashMap<>();

    private final Map<UserState, IStateTextMessageHandler> stateIStateTextMessageHandlerMap = new HashMap<>();

    public TextMessageHandler(List<ITextMessageHandler> textMessageHandlers,
                              ITextMessageTypeService textMessageTypeService,
                              IRedisUserStateService redisUserStateService,
                              List<IStateTextMessageHandler> stateTextMessageHandlers, IStartService startService) {
        this.textMessageTypeService = textMessageTypeService;
        this.redisUserStateService = redisUserStateService;
        this.startService = startService;
        for (ITextMessageHandler textMessageHandler : textMessageHandlers) {
            textMessageHandlersMap.put(textMessageHandler.getTextMessageType(), textMessageHandler);
        }
        for (IStateTextMessageHandler stateTextMessageHandler : stateTextMessageHandlers) {
            stateIStateTextMessageHandlerMap.put(stateTextMessageHandler.getUserState(), stateTextMessageHandler);
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        String text = message.getText();
        if (SlashCommand.START.getText().equals(text)) {
            startService.process(message.getChatId());
            return true;
        }
        UserState userState = redisUserStateService.get(message.getChatId());
        if (Objects.nonNull(userState)) {
            IStateTextMessageHandler stateTextMessageHandler = stateIStateTextMessageHandlerMap.get(userState);
            if (Objects.nonNull(stateTextMessageHandler)) {
                stateTextMessageHandler.handle(message);
                return true;
            }
            return false;
        }
        TextMessageType textMessageType = textMessageTypeService.fromText(text);
        if (Objects.nonNull(textMessageType)) {
            ITextMessageHandler textMessageHandler = textMessageHandlersMap.get(textMessageType);
            if (textMessageHandler != null) {
                return textMessageHandler.handle(message);
            }
        }
        return false;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.TEXT;
    }

}
