package tgb.btc.rce.service.processors.menu;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.Processor;

@CommandProcessor(command = Command.OBSERVER_PANEL)
public class ObserverPanel extends Processor {

    @Override
    public void run(Update update) {
        responseSender.sendMessage(updateService.getChatId(update), "Вы перешли в панель наблюдателя", Menu.OBSERVER_PANEL);
    }
}
