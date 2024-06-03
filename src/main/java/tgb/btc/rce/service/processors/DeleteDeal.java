package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.service.bean.bot.DealService;
import tgb.btc.library.service.schedule.DealDeleteScheduler;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.DELETE_DEAL)
@Slf4j
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
        Long dealPid = null;
        Long userChatId = dealService.getUserChatIdByDealPid(dealPid);
        dealService.deleteById(dealPid);
        log.info("Админ " + chatId + " удалил заявку " + dealPid + " пользователя " + userChatId);
        DealDeleteScheduler.deleteCryptoDeal(dealPid);
        userService.updateCurrentDealByChatId(null, chatId);
        responseSender.sendMessage(chatId, "Заявка удалена.");
    }
}
