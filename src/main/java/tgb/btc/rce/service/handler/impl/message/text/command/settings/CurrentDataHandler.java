package tgb.btc.rce.service.handler.impl.message.text.command.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.BotMessage;
import tgb.btc.library.constants.enums.bot.BotMessageType;
import tgb.btc.library.interfaces.service.bean.bot.IBotMessageService;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

@Service
public class CurrentDataHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final IBotMessageService botMessageService;

    public CurrentDataHandler(IResponseSender responseSender, IBotMessageService botMessageService) {
        this.responseSender = responseSender;
        this.botMessageService = botMessageService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        responseSender.sendMessage(message.getChatId(), "Сообщения:");
        BotMessageType[] messageTypes = BotMessageType.values();
        for(BotMessageType botMessageType : messageTypes) {
            BotMessage botMessage = botMessageService.findByTypeNullSafe(botMessageType);
            responseSender.sendMessage(chatId, botMessageType.getDisplayName());
            switch (botMessage.getMessageType()) {
                case TEXT:
                    responseSender.sendMessage(chatId, botMessage.getText());
                    break;
                case IMAGE:
                    responseSender.sendPhoto(chatId, botMessage.getText(), botMessage.getImage());
                    break;
                case ANIMATION:
                    responseSender.sendAnimation(chatId, botMessage.getText(), botMessage.getAnimation(), null);
                    break;
            }
        }
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.CURRENT_DATA;
    }
}
