package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.library.repository.web.ApiDealRepository;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.support.DealSupportService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

import static tgb.btc.rce.constants.BotStringConstants.CALLBACK_DATA_SPLITTER;

@CommandProcessor(command = Command.NEW_API_DEALS)
public class NewApiDeals extends Processor {

    private ApiDealRepository apiDealRepository;

    private DealSupportService dealSupportService;

    @Autowired
    public void setDealSupportService(DealSupportService dealSupportService) {
        this.dealSupportService = dealSupportService;
    }

    @Autowired
    public void setApiDealRepository(ApiDealRepository apiDealRepository) {
        this.apiDealRepository = apiDealRepository;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        List<Long> activeDeals = apiDealRepository.getActiveDealsPids();

        if (activeDeals.isEmpty()) {
            responseSender.sendMessage(chatId, "Новых заявок нет.");
            return;
        }

        activeDeals.forEach(pid -> {
            String dealInfo = dealSupportService.apiDealToString(pid);
            responseSender.sendMessage(chatId, dealInfo, KeyboardUtil.buildInline(List.of(
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
