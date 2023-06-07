package tgb.btc.lib.service.processors;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.QUIT_ADMIN_PANEL)
public class QuitAdminPanel extends Processor {

    @Override
    public void run(Update update) {
        processToMainMenu(UpdateUtil.getChatId(update));
    }
}
