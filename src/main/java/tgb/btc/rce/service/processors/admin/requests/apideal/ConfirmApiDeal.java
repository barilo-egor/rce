package tgb.btc.rce.service.processors.admin.requests.apideal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.web.ApiDealStatus;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.CONFIRM_API_DEAL)
@Slf4j
public class ConfirmApiDeal extends Processor {

    private IApiDealService apiDealService;

    @Autowired
    public void setApiDealService(IApiDealService apiDealService) {
        this.apiDealService = apiDealService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.deleteMessage(chatId, CallbackQueryUtil.messageId(update));
        Long dealPid = CallbackQueryUtil.getSplitLongData(update, 1);
        apiDealService.updateApiDealStatusByPid(ApiDealStatus.ACCEPTED, dealPid);
        log.debug("Админ chatId={} подтвердил АПИ сделку={}.", chatId, dealPid);
        responseSender.sendMessage(chatId, "API сделка подтверждена.");
    }
}
