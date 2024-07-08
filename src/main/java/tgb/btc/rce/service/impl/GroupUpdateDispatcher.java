package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import tgb.btc.library.bean.bot.GroupChat;
import tgb.btc.library.constants.enums.MemberStatus;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.rce.service.IGroupUpdateDispatcher;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.util.TelegramBotPropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class GroupUpdateDispatcher implements IGroupUpdateDispatcher {

    private IGroupChatService groupChatService;

    private IResponseSender responseSender;

    @Autowired
    public void setResponseSender(@Lazy IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setGroupChatService(IGroupChatService groupChatService) {
        this.groupChatService = groupChatService;
    }

    @Override
    public void dispatch(Update update) {
        Long chatId = UpdateUtil.getGroupChatId(update);
        if (Objects.isNull(chatId)) {
            log.warn("Chat id группы не был найден. Update:{}", update);
            return;
        }
        if (update.hasMyChatMember()) {
            ChatMember newChatMember = update.getMyChatMember().getNewChatMember();
            if (TelegramBotPropertiesUtil.getUsername().equals(newChatMember.getUser().getUserName())) {
                MemberStatus status = MemberStatus.valueOf(newChatMember.getStatus().toUpperCase());
                Optional<GroupChat> groupChatOptional = groupChatService.find(chatId);
                if (groupChatOptional.isPresent()) {
                    groupChatService.updateMemberStatus(chatId, status);
                } else {
                    groupChatService.register(chatId, update.getMyChatMember().getChat().getTitle(), status);
                }
            }
        } else if (update.hasMessage() && StringUtils.isNotEmpty(update.getMessage().getNewChatTitle())) {
            groupChatService.updateTitleByChatId(chatId, update.getMessage().getNewChatTitle());
        } else if (update.hasMessage() && update.getMessage().isReply()
                && update.getMessage().getReplyToMessage().getChatId().equals(chatId)
                && update.getMessage().hasText()
                && update.getMessage().getText().equals("+")) {
            responseSender.sendEditedMessageText(chatId, update.getMessage().getReplyToMessage().getMessageId(),
                    "Обработано.", null);
        }
    }
}
