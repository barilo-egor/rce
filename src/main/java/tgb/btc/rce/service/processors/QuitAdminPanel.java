package tgb.btc.rce.service.processors;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.QUIT_ADMIN_PANEL)
public class QuitAdminPanel extends Processor {

    @Override
    public void run(Update update) {
        processToMainMenu(UpdateUtil.getChatId(update));
    }
}
