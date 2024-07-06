package tgb.btc.rce.service.impl.processors.role;

import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

public abstract class ChangeRoleProcessor extends Processor {

    protected void changeRole(Update update, UserRole userRole) {
        Long chatId = UpdateUtil.getChatId(update);
        Long userChatId = Long.parseLong(UpdateUtil.getMessageText(update).split(" ")[1]);
        if (!readUserService.existsByChatId(userChatId)) {
            responseSender.sendMessage(chatId, "Пользователь с таким ID не найден.");
            return;
        }
        if (readUserService.getUserRoleByChatId(userChatId).equals(userRole)) {
            responseSender.sendMessage(chatId, "Пользователь " + userChatId + " уже в роли \"" + userRole.getDisplayName() + "\".");
            return;
        }
        modifyUserService.updateUserRoleByChatId(userRole, userChatId);
        responseSender.sendMessage(chatId, "Пользователю " + userChatId + " сменена роль на \"" + userRole.getDisplayName() + "\".");
        responseSender.sendMessage(userChatId, "Вы были переведены в роль \"" + userRole.getDisplayName() + "\".");
        getLogger().debug("Админ {} сменил пользователю {} роль на {}.", chatId, userChatId, userRole.getDisplayName());
    }

    protected abstract Logger getLogger();
}
