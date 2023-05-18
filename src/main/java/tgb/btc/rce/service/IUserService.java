package tgb.btc.rce.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;

public interface IUserService {

    Integer getStepByChatId(Long chatId);

    Command getCommandByChatId(Long chatId);

    User register(Update update);

    boolean existByChatId(Long chatId);
}
