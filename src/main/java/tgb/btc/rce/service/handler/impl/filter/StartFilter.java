package tgb.btc.rce.service.handler.impl.filter;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.service.web.merchant.dashpay.DashPayMerchantService;
import tgb.btc.rce.enums.UpdateFilterType;
import tgb.btc.rce.service.handler.IUpdateFilter;
import tgb.btc.rce.service.handler.util.IStartService;
import tgb.btc.rce.service.process.IUserProcessService;

@Service
public class StartFilter implements IUpdateFilter {

    private final IStartService startService;

    private final IUserProcessService userProcessService;

    private final DashPayMerchantService dashPayMerchantService;

    private final IReadDealService readDealService;

    public StartFilter(IStartService startService, IUserProcessService userProcessService,
                       DashPayMerchantService dashPayMerchantService, IReadDealService readDealService) {
        this.startService = startService;
        this.userProcessService = userProcessService;
        this.dashPayMerchantService = dashPayMerchantService;
        this.readDealService = readDealService;
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
