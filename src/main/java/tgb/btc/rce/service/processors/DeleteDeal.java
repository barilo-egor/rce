package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.schedule.DealDeleteScheduler;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.DELETE_DEAL)
public class DeleteDeal extends Processor {

    private DealService dealService;

    @Autowired
    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        Long dealPid = dealService.getPidActiveDealByChatId(chatId);
        DealDeleteScheduler.deleteCryptoDeal(dealPid);
        dealService.deleteById(dealPid);
        DealDeleteScheduler.deleteCryptoDeal(dealPid);
        userService.updateCurrentDealByChatId(null, chatId);
        responseSender.sendMessage(chatId, "Заявка удалена.");
    }
}
