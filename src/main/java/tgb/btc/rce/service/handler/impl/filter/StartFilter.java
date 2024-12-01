package tgb.btc.rce.service.handler.impl.filter;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.UpdateFilterType;
import tgb.btc.rce.service.handler.IUpdateFilter;
import tgb.btc.rce.service.handler.util.IStartService;
import tgb.btc.rce.service.process.IUserProcessService;

@Service
public class StartFilter implements IUpdateFilter {

    private final IStartService startService;

    private final IUserProcessService userProcessService;

    public StartFilter(IStartService startService, IUserProcessService userProcessService) {
        this.startService = startService;
        this.userProcessService = userProcessService;
    }

    @Override
    public void handle(Update update) {
        userProcessService.registerIfNotExists(update);
        startService.process(update.getMessage().getChatId());
    }

    @Override
    public UpdateFilterType getType() {
        return UpdateFilterType.START;
    }
}
