package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.IUserService;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.START)
public class Start extends Processor {

    private final IUserService userService;

    @Autowired
    public Start(IResponseSender responseSender, IUserService userService) {
        super(responseSender);
        this.userService = userService;
    }

    @Override
    public void run(Update update) {
        userService.setDefaultValues(UpdateUtil.getChatId(update));
        responseSender.sendMessage(UpdateUtil.getChatId(update), "Hello.", getMenuKeyboard(update));
    }

    private ReplyKeyboard getMenuKeyboard(Update update) {
        return MenuFactory.build(Menu.MAIN, userService.isAdminByChatId(UpdateUtil.getChatId(update)));
    }
}
