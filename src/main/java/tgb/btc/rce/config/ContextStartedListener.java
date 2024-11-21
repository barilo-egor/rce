package tgb.btc.rce.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.service.INotifyService;

@Component
public class ContextStartedListener implements ApplicationListener<ApplicationReadyEvent> {

    private final INotifyService notifyService;

    public ContextStartedListener(INotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        notifyService.notifyMessage("\uD83D\uDFE2 Бот запущен.", UserRole.OPERATOR_ACCESS, false);
    }
}
