package tgb.btc.rce.service.processors.apideal;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.ApiDealStatus;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.repository.ApiDealRepository;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.CONFIRM_API_DEAL)
public class ConfirmApiDeal extends Processor {

    private ApiDealRepository apiDealRepository;

    @Autowired
    public void setApiDealRepository(ApiDealRepository apiDealRepository) {
        this.apiDealRepository = apiDealRepository;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.deleteMessage(chatId, CallbackQueryUtil.messageId(update));
        apiDealRepository.updateApiDealStatusByPid(ApiDealStatus.ACCEPTED, CallbackQueryUtil.getSplitLongData(update, 1));
        responseSender.sendMessage(chatId, "API сделка подтверждена.");
    }
}