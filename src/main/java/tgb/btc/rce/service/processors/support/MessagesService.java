package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.impl.ResponseSender;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.NumberUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.util.Objects;

@Service
public class MessagesService {

    private final ResponseSender responseSender;

    private final UserService userService;

    @Autowired
    public MessagesService(ResponseSender responseSender, UserService userService) {
        this.responseSender = responseSender;
        this.userService = userService;
    }

    public void askForChatId(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        userService.nextStep(chatId, Command.SEND_MESSAGE_TO_USER);
        responseSender.sendMessage(chatId, "Введите ID пользователя.",
                MenuFactory.build(Menu.ADMIN_BACK, userService.isAdminByChatId(chatId)));
    }
    
    public boolean isUserExist(Update update) {
        Long recipientChatId = UpdateUtil.getLongFromText(update);
        if (!userService.existByChatId(recipientChatId))
            throw new BaseException("Пользователь с таким чат айди не найден");
        return true;
    }

    public void askForMessageText(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        userService.nextStep(chatId, Command.SEND_MESSAGE_TO_USER);
        responseSender.sendMessage(chatId, "Введите текст сообщения.",
                MenuFactory.build(Menu.ADMIN_BACK, userService.isAdminByChatId(chatId)));
    }

    public void sendMessageToUser(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        try {
            responseSender.sendMessage(NumberUtil.getInputLong(userService.getBufferVariable(chatId)),
                    UpdateUtil.getMessageText(update));
            responseSender.sendMessage(chatId, "Сообщение отправлено.");
        } catch (Exception e) {
            responseSender.sendMessage(chatId, "Ошибка при отправке сообщения: " + e.getMessage());
        }
    }
}
