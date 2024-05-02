package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.service.bean.bot.DealService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.DELETE_DEAL_AND_BLOCK_USER)
@Slf4j
public class DeleteDealAndBlockUserProcessor extends Processor {

    private DealService dealService;

    private Start start;

    @Autowired
    public void setStart(Start start) {
        this.start = start;
    }

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
        log.info("Админ " + chatId + " удалил заявку " + dealPid + " пользователя " + userChatId + " и заблокировал его.");
        dealService.deleteDeal(dealPid, true);
        responseSender.sendMessage(chatId, "Заявка №" + dealPid + " удалена.");
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        start.run(userChatId);
    }
}
