package tgb.btc.rce.service.handler.impl.filter;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.UpdateFilterType;
import tgb.btc.rce.service.handler.IUpdateFilter;
import tgb.btc.rce.service.handler.util.IStartService;

@Service
public class StartFilter implements IUpdateFilter {

    private final IStartService startService;

    public StartFilter(IStartService startService) {
        this.startService = startService;
    }

    @Override
    public void handle(Update update) {
        startService.process(update.getMessage().getChatId());
    }

    @Override
    public UpdateFilterType getType() {
        return UpdateFilterType.START;
    }
}
