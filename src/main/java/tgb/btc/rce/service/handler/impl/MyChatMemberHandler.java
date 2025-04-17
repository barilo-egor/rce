package tgb.btc.rce.service.handler.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.service.handler.IUpdateHandler;

import java.util.Objects;

@Service
public class MyChatMemberHandler implements IUpdateHandler {

    private final String MEMBER = "member";

    private final String KICKED = "kicked";

    private final IModifyUserService modifyUserService;

    public MyChatMemberHandler(IModifyUserService modifyUserService) {
        this.modifyUserService = modifyUserService;
    }

    @Override
    public boolean handle(Update update) {
        ChatMemberUpdated chatMemberUpdated = update.getMyChatMember();
        if (Objects.nonNull(chatMemberUpdated.getNewChatMember())) {
            Long chatId = chatMemberUpdated.getFrom().getId();
            String status = chatMemberUpdated.getNewChatMember().getStatus();
            if (KICKED.equals(status)) {
                modifyUserService.updateIsActiveByChatId(false, chatId);
            } else if (MEMBER.equals(status)) {
                modifyUserService.updateIsActiveByChatId(true, chatId);
            }
        }
        return true;
    }

    @Override
    public UpdateType getUpdateType() {
        return UpdateType.MY_CHAT_MEMBER;
    }
}
