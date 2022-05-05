package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.START)
public class Start extends Processor {

    @Autowired
    public Start(IResponseSender responseSender) {
        super(responseSender);
    }

    @Override
    public void run(Update update) {
        responseSender.sendMessage(UpdateUtil.getChatId(update), "Hello.");
    }
}
