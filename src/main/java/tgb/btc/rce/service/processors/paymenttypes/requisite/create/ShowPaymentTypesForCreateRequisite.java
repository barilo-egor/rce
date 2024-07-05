package tgb.btc.rce.service.processors.paymenttypes.requisite.create;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.IUserDataService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.NEW_PAYMENT_TYPE_REQUISITE, step = 2)
@Slf4j
public class ShowPaymentTypesForCreateRequisite extends Processor {

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
        userDataService.updateLongByUserPid(readUserService.getPidByChatId(chatId), Long.parseLong(values[1]));
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        modifyUserService.nextStep(chatId);
        responseSender.sendMessage(chatId, "Введите реквизит.");
    }

}
