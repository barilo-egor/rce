package tgb.btc.rce.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.bot.UserRole;

import java.util.Set;

public interface INotifyService {

    void notifyMessage(String message, Set<UserRole> roles);

    void notifyMessageAndPhoto(String message, String imageId, Set<UserRole> roles);

    void notifyMessage(String message, ReplyKeyboard replyKeyboard, Set<UserRole> roles);

    void notifyMessage(String message, String data, Set<UserRole> roles);
}
