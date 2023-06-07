package tgb.btc.lib.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.bean.BotMessage;
import tgb.btc.lib.enums.BotMessageType;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.impl.BotMessageService;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.CURRENT_DATA)
public class CurrentData extends Processor {

    private BotMessageService botMessageService;

    @Autowired
    public void setBotMessageService(BotMessageService botMessageService) {
        this.botMessageService = botMessageService;
    }

    @Override
    public void run(Update update) {
        responseSender.sendMessage(UpdateUtil.getChatId(update), "Сообщения:");
        BotMessageType[] messageTypes = BotMessageType.values();
        for(BotMessageType botMessageType : messageTypes) {
            BotMessage botMessage = botMessageService.findByType(botMessageType);
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
