package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.service.processors.support.MessagesService;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.NumberUtil;
import tgb.btc.rce.util.UpdateUtil;

@Slf4j
@CommandProcessor(command = Command.BAN_UNBAN)
public class BanUnban extends Processor {

    private final MessagesService messagesService;

    @Autowired
    public BanUnban(IResponseSender responseSender, UserService userService, MessagesService messagesService) {
        super(responseSender, userService);
        this.messagesService = messagesService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        checkForCancel(update);
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                messagesService.askForChatId(update, Command.BAN_UNBAN);
                break;
            case 1:
                banUser(update);
                processToAdminMainPanel(chatId);
                break;
        }
    }

    private void banUser(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Long inputChatId = NumberUtil.getInputLong(UpdateUtil.getMessageText(update));
        if (!userService.existByChatId(inputChatId)) {
            responseSender.sendMessage(chatId, "Пользователь с таким ID не найден.",
                    MenuFactory.build(Menu.ADMIN_BACK, userService.isAdminByChatId(chatId)));
            return;
        }
        User user = userService.findByChatId(inputChatId);
        if(!user.getBanned()) {
            user.setBanned(true);
            responseSender.sendMessage(chatId,
                    "Пользователь заблокирован.");
            log.debug("Админ " + chatId + " забанил пользователя " + user.getChatId());
        } else {
            user.setBanned(false);
            responseSender.sendMessage(UpdateUtil.getChatId(update),
                    "Пользователь разблокирован.");
            log.debug("Админ " + chatId + " разбанил пользователя " + user.getChatId());
        }
        userService.save(user);
    }
}
