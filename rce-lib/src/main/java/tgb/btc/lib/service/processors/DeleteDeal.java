package tgb.btc.lib.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.impl.DealService;
import tgb.btc.lib.service.schedule.DealDeleteScheduler;
import tgb.btc.lib.util.UpdateUtil;

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
