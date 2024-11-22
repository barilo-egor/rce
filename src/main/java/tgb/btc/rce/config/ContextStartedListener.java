package tgb.btc.rce.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.service.INotifyService;

@Component
public class ContextStartedListener implements ApplicationListener<ApplicationReadyEvent> {

    private final INotifyService notifyService;

    private final CacheManager cacheManager;

    public ContextStartedListener(INotifyService notifyService, CacheManager cacheManager) {
        this.notifyService = notifyService;
        this.cacheManager = cacheManager;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        cacheManager.getCacheNames().forEach(name -> {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
        notifyService.notifyMessage("\uD83D\uDFE2 Бот запущен.", UserRole.OPERATOR_ACCESS, false);
    }
}
