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
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.impl.ResponseSender;
import tgb.btc.rce.service.util.IMenuService;


@Service
public class MessagesService {

    private ResponseSender responseSender;

    private IReadUserService readUserService;

    private IModifyUserService modifyUserService;

    private IMenuService menuService;
    
    private IUpdateService updateService;

    @Autowired
    public void setUpdateService(IUpdateService updateService) {
        this.updateService = updateService;
    }

    @Autowired
    public void setMenuService(IMenuService menuService) {
        this.menuService = menuService;
    }

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
        Long chatId = updateService.getChatId(update);
        modifyUserService.nextStep(chatId, command.name());
        responseSender.sendMessage(chatId, "Введите ID пользователя.",
                menuService.build(Menu.ADMIN_BACK, readUserService.getUserRoleByChatId(chatId)));
    }

    public void askForDealsCount(Update update, Command command) {
        Long chatId = updateService.getChatId(update);
        modifyUserService.nextStep(chatId, command.name());
        responseSender.sendMessage(chatId, "Введите кол-во возможных сделок.",
                menuService.build(Menu.ADMIN_BACK, readUserService.getUserRoleByChatId(chatId)));
    }

    public boolean isUserExist(Update update) {
        Long recipientChatId = updateService.getLongFromText(update);
        if (!readUserService.existsByChatId(recipientChatId))
            throw new BaseException("Пользователь с таким чат айди не найден");
        modifyUserService.updateBufferVariable(updateService.getChatId(update), recipientChatId.toString());
        return true;
    }

    public void askForMessageText(Update update, Command command) {
        Long chatId = updateService.getChatId(update);
        modifyUserService.nextStep(chatId, command.name());
        responseSender.sendMessage(chatId, "Введите текст сообщения.",
                menuService.build(Menu.ADMIN_BACK, readUserService.getUserRoleByChatId(chatId)));
    }

    public void sendMessageToUser(Update update) {
        Long chatId = updateService.getChatId(update);
        try {
            responseSender.sendMessage(Long.parseLong(readUserService.getBufferVariable(chatId)),
                    updateService.getMessageText(update));
            responseSender.sendMessage(chatId, "Сообщение отправлено.");
        } catch (Exception e) {
            responseSender.sendMessage(chatId, "Ошибка при отправке сообщения: " + e.getMessage());
        }
    }

    @Async
    public void sendMessageToUsers(Update update) {
        Long chatId = updateService.getChatId(update);
        readUserService.getChatIdsForMailing()
                .forEach(userChatId -> {
                    try {
                        responseSender.sendMessageThrows(userChatId, updateService.getMessageText(update));
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
