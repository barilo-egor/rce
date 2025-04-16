package tgb.btc.rce.service.handler.impl.filter;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.UpdateFilterType;
import tgb.btc.rce.service.handler.IUpdateFilter;
import tgb.btc.rce.service.handler.util.IStartService;
import tgb.btc.rce.vo.TelegramUpdateEvent;

@Service
public class MainMenuFilter implements IUpdateFilter {

    private final IStartService startService;

    private final ApplicationEventPublisher eventPublisher;

    public MainMenuFilter(IStartService startService, ApplicationEventPublisher eventPublisher) {
        this.startService = startService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void handle(Update update) {
        startService.processToStartState(update.getMessage().getChatId());
        eventPublisher.publishEvent(new TelegramUpdateEvent(this, update));
    }

    @Override
    public UpdateFilterType getType() {
        return UpdateFilterType.MAIN_MENU;
    }
}
