package tgb.btc.rce.service.processors.admin.requests.apideal;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.web.ApiDealStatus;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.support.DealSupportService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

import static tgb.btc.rce.constants.BotStringConstants.CALLBACK_DATA_SPLITTER;

@CommandProcessor(command = Command.SHOW_API_DEAL)
public class ShowApiDeal extends Processor {

    private DealSupportService dealSupportService;

    private IApiDealService apiDealService;

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
        if (!update.hasCallbackQuery()) return;
        Long chatId = updateService.getChatId(update);
        try {
            responseSender.deleteMessage(chatId, updateService.getMessage(update).getMessageId());
        } catch (Exception ignored) {
        }
        Long pid = Long.parseLong(update.getCallbackQuery().getData().split(CALLBACK_DATA_SPLITTER)[1]);
        ApiDealStatus status = apiDealService.getApiDealStatusByPid(pid);
        if (!ApiDealStatus.PAID.equals(status)) {
            responseSender.sendMessage(chatId, "Заяка уже обработана, либо отменена.");
            return;
        }
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
    }
}
