package tgb.btc.rce.service.processors.admin.requests.apideal;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.support.DealSupportService;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

import static tgb.btc.rce.constants.BotStringConstants.CALLBACK_DATA_SPLITTER;

@CommandProcessor(command = Command.NEW_API_DEALS)
public class NewApiDeals extends Processor {

    private IApiDealService apiDealService;

    private DealSupportService dealSupportService;

    @Autowired
    public void setApiDealService(IApiDealService apiDealService) {
        this.apiDealService = apiDealService;
    }

    @Autowired
    public void setDealSupportService(DealSupportService dealSupportService) {
        this.dealSupportService = dealSupportService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        List<Long> activeDeals = apiDealService.getActiveDealsPids();

        if (activeDeals.isEmpty()) {
            responseSender.sendMessage(chatId, "Новых заявок нет.");
            return;
        }

        activeDeals.forEach(pid -> {
            String dealInfo = dealSupportService.apiDealToString(pid);
            responseSender.sendMessage(chatId, dealInfo, keyboardBuildService.buildInline(List.of(
                    InlineButton.builder()
                            .text("Подтвердить")
                            .data(Command.CONFIRM_API_DEAL.getText() + CALLBACK_DATA_SPLITTER + pid)
                            .build(),
                    InlineButton.builder()
                            .text("Отклонить")
                            .data(Command.CANCEL_API_DEAL.getText() + CALLBACK_DATA_SPLITTER + pid)
                            .build()
            )));
        });
    }
}
