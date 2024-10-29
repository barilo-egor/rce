package tgb.btc.rce.service.processors.admin.requests.apideal;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.process.IApiDealBotService;

import java.util.List;

@CommandProcessor(command = Command.NEW_API_DEALS)
public class NewApiDeals extends Processor {

    private IApiDealService apiDealService;

    private IApiDealBotService apiDealBotService;

    @Autowired
    public void setApiDealBotService(IApiDealBotService apiDealBotService) {
        this.apiDealBotService = apiDealBotService;
    }

    @Autowired
    public void setApiDealService(IApiDealService apiDealService) {
        this.apiDealService = apiDealService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        List<Long> activeDeals = apiDealService.getActiveDealsPids();

        if (activeDeals.isEmpty()) {
            responseSender.sendMessage(chatId, "Новых заявок нет.");
            return;
        }

        activeDeals.forEach(pid -> apiDealBotService.sendApiDeal(pid, chatId));
    }
}
