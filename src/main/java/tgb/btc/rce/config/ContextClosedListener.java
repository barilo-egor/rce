package tgb.btc.rce.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.service.INotifyService;

@Component
public class ContextClosedListener implements ApplicationListener<ContextClosedEvent> {

    private final INotifyService notifyService;

    public ContextClosedListener(INotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @Override
    public void onApplicationEvent(@NonNull ContextClosedEvent event) {
        notifyService.notifyMessage("⚠️ Рестарт бота.", UserRole.OPERATOR_ACCESS, false);
    }
}
