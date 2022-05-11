package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.BotMessageType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.IBotMessageService;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.DRAWS)
public class Draws extends Processor {

    private final UserService userService;
    private final IBotMessageService botMessageService;

    @Autowired
    public Draws(IResponseSender responseSender, UserService userService, IBotMessageService botMessageService) {
        super(responseSender);
        this.userService = userService;
        this.botMessageService = botMessageService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendBotMessage(botMessageService.findByType(BotMessageType.DRAWS),
                chatId,
                getDrawsKeyboard(chatId));
    }

    private ReplyKeyboard getDrawsKeyboard(Long chatId) {
        return MenuFactory.build(Menu.DRAWS, userService.isAdminByChatId(chatId));
    }
}
