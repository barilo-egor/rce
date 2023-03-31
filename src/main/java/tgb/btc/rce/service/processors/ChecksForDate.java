package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.CHECKS_FOR_DATE)
public class ChecksForDate extends Processor {

    @Autowired
    public ChecksForDate(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        userService.nextStep(chatId);
        responseSender.sendMessage(chatId, "Введите дату в формате 01.01.2000");
    }

}
