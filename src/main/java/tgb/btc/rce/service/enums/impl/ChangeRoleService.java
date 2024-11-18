package tgb.btc.rce.service.enums.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.enums.IChangeRoleService;
import tgb.btc.rce.service.handler.service.IStartService;

@Service
@Slf4j
public class ChangeRoleService implements IChangeRoleService {

    private final IResponseSender responseSender;

    private final IReadUserService readUserService;

    private final IModifyUserService modifyUserService;

    private final IStartService startService;

    public ChangeRoleService(IResponseSender responseSender, IReadUserService readUserService,
                             IModifyUserService modifyUserService, IStartService startService) {
        this.responseSender = responseSender;
        this.readUserService = readUserService;
        this.modifyUserService = modifyUserService;
        this.startService = startService;
    }

    @Override
    public void changeRole(Message message, UserRole role) {
        Long chatId = message.getChatId();
        Long userChatId;
        String[] split = message.getText().split(" ");
        if (split.length <= 1) {
            responseSender.sendMessage(chatId, """
                    Введите chatId пользователя после команды. Пример:
                    /makeuser 12345678
                    """);
            return;
        }
        try {
            userChatId = Long.parseLong(message.getText().split(" ")[1]);
        } catch (NumberFormatException e) {
            responseSender.sendMessage(chatId, "Неверный формат chat id.");
            return;
        }
        if (!readUserService.existsByChatId(userChatId)) {
            responseSender.sendMessage(chatId, "Пользователь с таким ID не найден.");
            return;
        }
        if (readUserService.getUserRoleByChatId(userChatId).equals(role)) {
            responseSender.sendMessage(chatId, "Пользователь " + userChatId + " уже в роли \"" + role.getDisplayName() + "\".");
            return;
        }
        modifyUserService.updateUserRoleByChatId(role, userChatId);
        responseSender.sendMessage(chatId, "Пользователю " + userChatId + " сменена роль на \"" + role.getDisplayName() + "\".");
        responseSender.sendMessage(userChatId, "Вы были переведены в роль \"" + role.getDisplayName() + "\".");
        startService.process(chatId);
        log.debug("Админ {} сменил пользователю {} роль на {}.", chatId, userChatId, role.getDisplayName());
    }
}
