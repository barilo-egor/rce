package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.sender.ResponseSender;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.NumberUtil;
import tgb.btc.rce.util.UpdateUtil;

@Service
public class MessagesService {

    private ResponseSender responseSender;

    private IReadUserService readUserService;

    private IModifyUserService modifyUserService;

    @Autowired
    public void setResponseSender(ResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
    }

    @Autowired
    public void setModifyUserService(IModifyUserService modifyUserService) {
        this.modifyUserService = modifyUserService;
    }

    public void askForChatId(Update update, Command command) {
        Long chatId = UpdateUtil.getChatId(update);
        modifyUserService.nextStep(chatId, command.name());
        responseSender.sendMessage(chatId, "Введите ID пользователя.",
                MenuFactory.build(Menu.ADMIN_BACK, readUserService.isAdminByChatId(chatId)));
    }

    public void askForDealsCount(Update update, Command command) {
        Long chatId = UpdateUtil.getChatId(update);
        modifyUserService.nextStep(chatId, command.name());
        responseSender.sendMessage(chatId, "Введите кол-во возможных сделок.",
                MenuFactory.build(Menu.ADMIN_BACK, readUserService.isAdminByChatId(chatId)));
    }

    public boolean isUserExist(Update update) {
        Long recipientChatId = UpdateUtil.getLongFromText(update);
        if (!readUserService.existsByChatId(recipientChatId))
            throw new BaseException("Пользователь с таким чат айди не найден");
        modifyUserService.updateBufferVariable(UpdateUtil.getChatId(update), recipientChatId.toString());
        return true;
    }

    public void askForMessageText(Update update, Command command) {
        Long chatId = UpdateUtil.getChatId(update);
        modifyUserService.nextStep(chatId, command.name());
        responseSender.sendMessage(chatId, "Введите текст сообщения.",
                MenuFactory.build(Menu.ADMIN_BACK, readUserService.isAdminByChatId(chatId)));
    }

    public void sendMessageToUser(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        try {
            responseSender.sendMessage(NumberUtil.getInputLong(readUserService.getBufferVariable(chatId)),
                    UpdateUtil.getMessageText(update));
            responseSender.sendMessage(chatId, "Сообщение отправлено.");
        } catch (Exception e) {
            responseSender.sendMessage(chatId, "Ошибка при отправке сообщения: " + e.getMessage());
        }
    }

    @Async
    public void sendMessageToUsers(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        readUserService.getChatIdsNotAdminsAndIsActiveAndNotBanned()
                .forEach(userChatId -> {
                    try {
                        responseSender.sendMessageThrows(userChatId, UpdateUtil.getMessageText(update));
                    } catch (TelegramApiException e) {
                        if (e instanceof TelegramApiRequestException) {
                            TelegramApiRequestException exception = (TelegramApiRequestException) e;
                            if (exception.getApiResponse().contains("bot was blocked by the user")) {
                                modifyUserService.updateIsActiveByChatId(false, userChatId);
                            }
                        }
                    }
                });
        responseSender.sendMessage(chatId, "Рассылка произведена.");
    }
}
