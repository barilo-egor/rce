package tgb.btc.rce.service.handler.impl.message.text.slash;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ISlashCommandHandler;

@Service
public class ChatIdHandler implements ISlashCommandHandler {

    private final IResponseSender responseSender;

    public ChatIdHandler(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        String chatIdMessage = """
                Ваш chat id - <code>%s</code>.
                Нажмите на chat id для копирования в буфер обмена.
                """;
        responseSender.sendMessage(chatId, chatIdMessage.formatted(chatId), "html");
    }

    @Override
    public SlashCommand getSlashCommand() {
        return SlashCommand.CHAT_ID;
    }
}
