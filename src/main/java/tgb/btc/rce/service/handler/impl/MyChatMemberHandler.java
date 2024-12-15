package tgb.btc.rce.service.handler.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.service.handler.IUpdateHandler;

@Service
public class MyChatMemberHandler implements IUpdateHandler {

    @Override
    public boolean handle(Update update) {
        return true;
    }

    @Override
    public UpdateType getUpdateType() {
        return UpdateType.MY_CHAT_MEMBER;
    }
}
