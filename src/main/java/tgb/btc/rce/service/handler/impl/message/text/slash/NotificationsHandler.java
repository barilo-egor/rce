package tgb.btc.rce.service.handler.impl.message.text.slash;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ISlashCommandHandler;

@Service
public class NotificationsHandler implements ISlashCommandHandler {

    private final IReadUserService readUserService;

    private final IModifyUserService modifyUserService;

    private final IResponseSender responseSender;

    public NotificationsHandler(IReadUserService readUserService, IModifyUserService modifyUserService,
                                IResponseSender responseSender) {
        this.readUserService = readUserService;
        this.modifyUserService = modifyUserService;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        User user = readUserService.findByChatId(chatId);
        boolean newValue = BooleanUtils.isFalse(user.getNotificationsOn());
        user.setNotificationsOn(newValue);
        modifyUserService.save(user);
        responseSender.sendMessage(chatId, "Оповещения были " + (newValue ? "включены." : "выключены."));
    }

    @Override
    public SlashCommand getSlashCommand() {
        return SlashCommand.NOTIFICATIONS;
    }
}
