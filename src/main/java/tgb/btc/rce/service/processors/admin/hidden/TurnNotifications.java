package tgb.btc.rce.service.processors.admin.hidden;

import org.apache.commons.lang.BooleanUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.User;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;

@CommandProcessor(command = Command.TURN_NOTIFICATIONS)
public class TurnNotifications extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        User user = readUserService.findByChatId(chatId);
        if (BooleanUtils.isFalse(user.getNotificationsOn())) {
            user.setNotificationsOn(true);
            modifyUserService.save(user);
            responseSender.sendMessage(chatId, "Оповещения были включены.");
        } else {
            user.setNotificationsOn(false);
            modifyUserService.save(user);
            responseSender.sendMessage(chatId, "Оповещения были выключены.");
        }
    }
}
