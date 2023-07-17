package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.ApiDealStatus;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.repository.ApiDealRepository;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.support.DealSupportService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.SHOW_API_DEAL)
public class ShowApiDeal extends Processor {

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
        if (!update.hasCallbackQuery()) return;
        Long chatId = UpdateUtil.getChatId(update);
        try {
            responseSender.deleteMessage(chatId, UpdateUtil.getMessage(update).getMessageId());
        } catch (Exception ignored) {
        }
        Long pid = Long.parseLong(update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]);
        ApiDealStatus status = apiDealRepository.getApiDealStatusByPid(pid);
        if (!ApiDealStatus.PAID.equals(status)) {
            responseSender.sendMessage(chatId, "Заяка уже обработана, либо отменена.");
            return;
        }
        String dealInfo = dealSupportService.apiDealToString(pid);
        responseSender.sendMessage(chatId, dealInfo);
    }
}
