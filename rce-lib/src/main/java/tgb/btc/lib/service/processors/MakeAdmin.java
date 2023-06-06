package tgb.btc.lib.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

@Slf4j
@CommandProcessor(command = Command.MAKE_ADMIN)
public class MakeAdmin extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Long userChatId = Long.parseLong(UpdateUtil.getMessageText(update).split(" ")[1]);
        if (!userService.existByChatId(userChatId)) {
            responseSender.sendMessage(chatId, "Пользователь с таким ID не найден.");
            return;
        }
        if (userRepository.isAdminByChatId(userChatId)) {
            responseSender.sendMessage(chatId, "Пользователь " + userChatId + " уже является админом.");
            return;
        }
        userRepository.updateIsAdminByChatId(userChatId, true);
        responseSender.sendMessage(chatId, "Пользователь " + userChatId + " стал админом.");
        log.debug("Админ {} сделал пользователя {} админом.", chatId, userChatId);
    }

}
