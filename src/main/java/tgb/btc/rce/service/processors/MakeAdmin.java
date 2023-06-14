package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

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
        if (BooleanUtils.isTrue(userRepository.isAdminByChatId(userChatId))) {
            responseSender.sendMessage(chatId, "Пользователь " + userChatId + " уже является админом.");
            return;
        }
        userRepository.updateIsAdminByChatId(userChatId, true);
        responseSender.sendMessage(chatId, "Пользователь " + userChatId + " стал админом.");
        log.debug("Админ {} сделал пользователя {} админом.", chatId, userChatId);
    }

}
