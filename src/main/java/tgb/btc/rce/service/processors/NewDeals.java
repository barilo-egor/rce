package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.service.processors.support.DealSupportService;
import tgb.btc.rce.util.UpdateUtil;

import java.util.List;

@CommandProcessor(command = Command.NEW_DEALS)
public class NewDeals extends Processor {

    private final DealService dealService;
    private final DealSupportService dealSupportService;

    @Autowired
    public NewDeals(IResponseSender responseSender, UserService userService, DealService dealService, DealSupportService dealSupportService) {
        super(responseSender, userService);
        this.dealService = dealService;
        this.dealSupportService = dealSupportService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        List<Long> activeDeals = dealService.getActiveDealPids();

        if (activeDeals.isEmpty()) {
            responseSender.sendMessage(chatId, "Новых заявок нет.");
            return;
        }

        activeDeals.forEach(dealPid -> responseSender.sendMessage(chatId, dealSupportService.dealToString(dealPid),
                        dealSupportService.dealToStringButtons(dealPid)));
    }
}
