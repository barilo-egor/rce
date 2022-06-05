package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.service.processors.support.DealSupportService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.SHOW_DEAL)
public class ShowDeal extends Processor {

    private final DealSupportService dealSupportService;

    @Autowired
    public ShowDeal(IResponseSender responseSender, UserService userService, DealSupportService dealSupportService) {
        super(responseSender, userService);
        this.dealSupportService = dealSupportService;
    }

    @Override
    public void run(Update update) {
        if (!update.hasCallbackQuery()) return;
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.deleteMessage(chatId, UpdateUtil.getMessage(update).getMessageId());
        String dealInfo = dealSupportService.dealToString(
                Long.parseLong(update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]));
        responseSender.sendMessage(chatId, dealInfo);
    }
}
