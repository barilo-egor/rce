package tgb.btc.rce.service.handler.filter;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.MessageImage;
import tgb.btc.rce.enums.UpdateFilterType;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IMessageImageResponseSender;
import tgb.btc.rce.service.handler.IUpdateFilter;

@Service
public class BotOffedFilter implements IUpdateFilter {

    private final IMessageImageResponseSender messageImageResponseSender;

    public BotOffedFilter(IMessageImageResponseSender messageImageResponseSender) {
        this.messageImageResponseSender = messageImageResponseSender;
    }

    @Override
    public void handle(Update update) {
        messageImageResponseSender.sendMessage(MessageImage.BOT_OFF, UpdateType.getChatId(update));
    }

    @Override
    public UpdateFilterType getType() {
        return UpdateFilterType.BOT_OFFED;
    }
}
