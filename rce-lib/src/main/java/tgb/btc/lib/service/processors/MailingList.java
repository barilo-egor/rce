package tgb.btc.lib.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.processors.support.MessagesService;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.MAILING_LIST)
public class MailingList extends Processor {

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
                messagesService.askForMessageText(update, Command.MAILING_LIST);
                break;
            case 1:
                if (!update.hasMessage() || !update.getMessage().hasText()) {
                    responseSender.sendMessage(chatId, "Введите текст для рассылки.");
                    return;
                }
                messagesService.sendMessageToUsers(update);
                processToAdminMainPanel(UpdateUtil.getChatId(update));
                break;
        }
    }
}
