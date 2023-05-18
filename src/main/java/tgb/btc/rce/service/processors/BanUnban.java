package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.support.MessagesService;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.NumberUtil;
import tgb.btc.rce.util.UpdateUtil;

@Slf4j
@CommandProcessor(command = Command.BAN_UNBAN)
public class BanUnban extends Processor {

    private MessagesService messagesService;

    @Autowired
    public void setMessagesService(MessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) return;
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
        if(!userService.getIsBannedByChatId(inputChatId)) {
            userService.ban(inputChatId);
            responseSender.sendMessage(chatId,
                    "Пользователь заблокирован.");
            log.debug("Админ " + chatId + " забанил пользователя " + inputChatId);
        } else {
            userService.unban(inputChatId);
            responseSender.sendMessage(UpdateUtil.getChatId(update),
                    "Пользователь разблокирован.");
            log.debug("Админ " + chatId + " разбанил пользователя " + inputChatId);
        }
    }
}
