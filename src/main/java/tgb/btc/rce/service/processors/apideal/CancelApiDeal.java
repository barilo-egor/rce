package tgb.btc.rce.service.processors.apideal;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.web.ApiDealStatus;
import tgb.btc.library.repository.web.ApiDealRepository;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.CANCEL_API_DEAL)
@Slf4j
public class CancelApiDeal extends Processor {

    private ApiDealRepository apiDealRepository;

    @Autowired
    public void setApiDealRepository(ApiDealRepository apiDealRepository) {
        this.apiDealRepository = apiDealRepository;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.deleteMessage(chatId, CallbackQueryUtil.messageId(update));
        Long dealPid = CallbackQueryUtil.getSplitLongData(update, 1);
        apiDealRepository.updateApiDealStatusByPid(ApiDealStatus.DECLINED, dealPid);
        log.debug("Админ chatId={} отменил АПИ сделку={}.", chatId, dealPid);
        responseSender.sendMessage(chatId, "API сделка отменена.");
    }
}
