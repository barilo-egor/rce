package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.BotMessageType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.BotMessageService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.BACK)
public class Back extends Processor {

    private final UserService userService;
    private final BotMessageService botMessageService;

    @Autowired
    public Back(IResponseSender responseSender, UserService userService, BotMessageService botMessageService) {
        super(responseSender);
        this.userService = userService;
        this.botMessageService = botMessageService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendBotMessage(botMessageService.findByType(BotMessageType.START),
                chatId,
                getMenuKeyboard(chatId));
    }

    private ReplyKeyboard getMenuKeyboard(Long chatId) {
        return MenuFactory.build(Menu.MAIN, userService.isAdminByChatId(chatId));
    }
}
