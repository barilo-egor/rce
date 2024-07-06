package tgb.btc.rce.service.impl.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.BotMessage;
import tgb.btc.library.constants.enums.bot.BotMessageType;
import tgb.btc.library.interfaces.service.bean.bot.IBotMessageService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.CURRENT_DATA)
public class CurrentData extends Processor {

    private IBotMessageService botMessageService;

    @Autowired
    public void setBotMessageService(IBotMessageService botMessageService) {
        this.botMessageService = botMessageService;
    }

    @Override
    public void run(Update update) {
        responseSender.sendMessage(UpdateUtil.getChatId(update), "Сообщения:");
        BotMessageType[] messageTypes = BotMessageType.values();
        for(BotMessageType botMessageType : messageTypes) {
            BotMessage botMessage = botMessageService.findByTypeNullSafe(botMessageType);
            responseSender.sendMessage(UpdateUtil.getChatId(update), botMessageType.getDisplayName());
            switch (botMessage.getMessageType()) {
                case TEXT:
                    responseSender.sendMessage(UpdateUtil.getChatId(update), botMessage.getText());
                    break;
                case IMAGE:
                    responseSender.sendPhoto(UpdateUtil.getChatId(update), botMessage.getText(), botMessage.getImage());
                    break;
                case ANIMATION:
                    responseSender.sendAnimation(UpdateUtil.getChatId(update), botMessage.getText(), botMessage.getAnimation(), null);
                    break;
            }
        }
    }
}
