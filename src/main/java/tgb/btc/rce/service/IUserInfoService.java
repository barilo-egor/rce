package tgb.btc.rce.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public interface IUserInfoService {
    void sendUserInformation(Long messageChatId, Long userChatId);

    void sendUserInformation(Long messageChatId, Long userChatId, ReplyKeyboard replyKeyboard);
}
