package tgb.btc.rce.service.impl.processors;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.ADMIN_BACK)
public class AdminBack extends Processor {

    @Override
    public void run(Update update) {
        processToAdminMainPanel(UpdateUtil.getChatId(update));
    }
}
