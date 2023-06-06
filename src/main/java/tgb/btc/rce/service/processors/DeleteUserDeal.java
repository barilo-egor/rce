package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.schedule.DealDeleteScheduler;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.DELETE_USER_DEAL)
public class DeleteUserDeal extends Processor {

    private DealService dealService;

    @Autowired
    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    @Override
    public void run(Update update) {
        if (!update.hasCallbackQuery()) return;
        Long chatId = UpdateUtil.getChatId(update);
        Long dealPid = Long.parseLong(
                update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]);
        Long userChatId = dealService.getUserChatIdByDealPid(dealPid);
        dealService.deleteById(dealPid);
        userService.updateCurrentDealByChatId(null, userChatId);
        DealDeleteScheduler.deleteCryptoDeal(dealPid);
        responseSender.sendMessage(chatId, "Заявка №" + dealPid + " удалена.");
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        responseSender.sendMessage(userChatId, MessagePropertiesUtil.getMessage("deal.deleted.by.admin"));
    }
}
