package tgb.btc.rce.service;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface IResponseSender {

    Message sendMessage(Long chatId, String text);
}
