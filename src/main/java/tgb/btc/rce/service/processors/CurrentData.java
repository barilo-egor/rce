package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.BotMessage;
import tgb.btc.library.constants.enums.bot.BotMessageType;
import tgb.btc.library.service.bean.bot.BotMessageService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

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
