package tgb.btc.rce.service.handler.impl.message.text.command;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.vo.TelegramUpdateEvent;

@Service
public class BuyTextCommandHandler implements ITextCommandHandler {

    private final ApplicationEventPublisher eventPublisher;

    private final IRedisUserStateService redisUserStateService;

    public BuyTextCommandHandler(ApplicationEventPublisher eventPublisher, IRedisUserStateService redisUserStateService) {
        this.eventPublisher = eventPublisher;
        this.redisUserStateService = redisUserStateService;
    }

    @Override
    public void handle(Message message) {
        Update update = new Update();
        update.setMessage(message);
        eventPublisher.publishEvent(new TelegramUpdateEvent(this, update));
        redisUserStateService.save(message.getChatId(), UserState.CREATING_A_DEAL);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.BUY;
    }
}
