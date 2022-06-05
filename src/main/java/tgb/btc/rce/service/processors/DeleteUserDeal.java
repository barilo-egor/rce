package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.DELETE_USER_DEAL)
public class DeleteUserDeal extends Processor {

    private final DealService dealService;

    @Autowired
    public DeleteUserDeal(IResponseSender responseSender, UserService userService, DealService dealService) {
        super(responseSender, userService);
        this.dealService = dealService;
    }

    @Override
    public void run(Update update) {
        if (!update.hasCallbackQuery()) return;
        Long chatId = UpdateUtil.getChatId(update);
        Long dealPid = Long.parseLong(
                update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]);
        dealService.deleteById(dealPid);
        responseSender.sendMessage(chatId, "Заявка №" + dealPid + " удалена.");
    }
}
