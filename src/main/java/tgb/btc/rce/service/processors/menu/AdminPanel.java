package tgb.btc.rce.service.processors.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.Processor;

@Service
public class AdminPanel extends Processor {

    @Override
    public void run(Update update) {
        responseSender.sendMessage(updateService.getChatId(update), PropertiesMessage.MENU_MAIN_ADMIN_MESSAGE, Menu.ADMIN_PANEL);
    }
}
