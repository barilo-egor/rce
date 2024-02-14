package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.library.constants.enums.web.ApiDealStatus;
import tgb.btc.rce.enums.Command;
import tgb.btc.library.repository.web.ApiDealRepository;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.service.processors.support.DealSupportService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

import static tgb.btc.rce.constants.BotStringConstants.CALLBACK_DATA_SPLITTER;

@CommandProcessor(command = Command.SHOW_API_DEAL)
public class ShowApiDeal extends Processor {

    private ApiDealRepository apiDealRepository;

    private DealSupportService dealSupportService;

    private KeyboardService keyboardService;

    @Autowired
    public void setKeyboardService(KeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

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
        if (!update.hasCallbackQuery()) return;
        Long chatId = UpdateUtil.getChatId(update);
        try {
            responseSender.deleteMessage(chatId, UpdateUtil.getMessage(update).getMessageId());
        } catch (Exception ignored) {
        }
        Long pid = Long.parseLong(update.getCallbackQuery().getData().split(CALLBACK_DATA_SPLITTER)[1]);
        ApiDealStatus status = apiDealRepository.getApiDealStatusByPid(pid);
        if (!ApiDealStatus.PAID.equals(status)) {
            responseSender.sendMessage(chatId, "Заяка уже обработана, либо отменена.");
            return;
        }
        String dealInfo = dealSupportService.apiDealToString(pid);
        responseSender.sendMessage(chatId, dealInfo, KeyboardUtil.buildInline(List.of(
                InlineButton.builder()
                        .text("Подтвердить")
                        .data(Command.CONFIRM_API_DEAL.name() + CALLBACK_DATA_SPLITTER + pid)
                        .build(),
                InlineButton.builder()
                        .text("Отклонить")
                        .data(Command.CANCEL_API_DEAL.name() + CALLBACK_DATA_SPLITTER + pid)
                        .build()
        )));
    }
}
