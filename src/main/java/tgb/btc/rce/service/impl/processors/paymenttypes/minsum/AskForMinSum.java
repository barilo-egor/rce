package tgb.btc.rce.service.impl.processors.paymenttypes.minsum;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.IUserDataService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.CHANGE_MIN_SUM, step = 3)
public class AskForMinSum extends Processor {

    private IUserDataService userDataService;

    @Autowired
    public void setUserDataService(IUserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (!update.hasCallbackQuery()) {
            responseSender.sendMessage(chatId, "Выберите тип оплаты.");
            return;
        }
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        Long paymentTypePid = Long.parseLong(values[1]);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        userDataService.updateLongByUserPid(readUserService.getPidByChatId(chatId), paymentTypePid);
        responseSender.sendMessage(chatId, "Введите минимальную сумму в рублях.");
        modifyUserService.nextStep(chatId);
    }
}
