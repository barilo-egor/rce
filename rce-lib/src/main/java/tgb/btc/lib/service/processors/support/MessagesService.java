package tgb.btc.lib.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.enums.Menu;
import tgb.btc.lib.exception.BaseException;
import tgb.btc.lib.service.impl.ResponseSender;
import tgb.btc.lib.service.impl.UserService;
import tgb.btc.lib.util.MenuFactory;
import tgb.btc.lib.util.NumberUtil;
import tgb.btc.lib.util.UpdateUtil;

@Service
public class MessagesService {

    private final ResponseSender responseSender;

    private final UserService userService;

    @Autowired
    public MessagesService(ResponseSender responseSender, UserService userService) {
        this.responseSender = responseSender;
        this.userService = userService;
    }

    public void askForChatId(Update update, Command command) {
        Long chatId = UpdateUtil.getChatId(update);
        userService.nextStep(chatId, command);
        responseSender.sendMessage(chatId, "Введите ID пользователя.",
                MenuFactory.build(Menu.ADMIN_BACK, userService.isAdminByChatId(chatId)));
    }

    public boolean isUserExist(Update update) {
        Long recipientChatId = UpdateUtil.getLongFromText(update);
        if (!userService.existByChatId(recipientChatId))
            throw new BaseException("Пользователь с таким чат айди не найден");
        userService.updateBufferVariable(UpdateUtil.getChatId(update), recipientChatId.toString());
        return true;
    }

    public void askForMessageText(Update update, Command command) {
        Long chatId = UpdateUtil.getChatId(update);
        userService.nextStep(chatId, command);
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

    @Async
    public void sendMessageToUsers(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        userService.getChatIdsNotAdminsAndIsActiveAndNotBanned()
                .forEach(userChatId -> {
                    try {
                        responseSender.sendMessageThrows(userChatId, UpdateUtil.getMessageText(update));
                    } catch (TelegramApiException e) {
                        if (e instanceof TelegramApiRequestException) {
                            TelegramApiRequestException exception = (TelegramApiRequestException) e;
                            if (exception.getApiResponse().contains("bot was blocked by the user")) {
                                userService.updateIsActiveByChatId(false, userChatId);
                            }
                        }
                    }
                });
        responseSender.sendMessage(chatId, "Рассылка произведена.");
    }
}
