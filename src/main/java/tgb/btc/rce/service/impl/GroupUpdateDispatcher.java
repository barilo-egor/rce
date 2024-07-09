package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberRestricted;
import tgb.btc.library.bean.bot.GroupChat;
import tgb.btc.library.constants.enums.MemberStatus;
import tgb.btc.library.constants.enums.bot.GroupChatType;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.rce.service.IGroupUpdateDispatcher;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.util.TelegramBotPropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class GroupUpdateDispatcher implements IGroupUpdateDispatcher {

    private IGroupChatService groupChatService;

    private IResponseSender responseSender;

    private final Pattern dealNumberPattern = Pattern.compile("№(\\d+)");

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
                if (MemberStatus.LEFT.equals(status) || MemberStatus.KICKED.equals(status)) {
                    log.debug("Бот был удален из группы chatid={}", chatId);
                    groupChatService.deleteByChatId(chatId);
                    return;
                }
                if (MemberStatus.RESTRICTED.equals(status)) {
                    boolean isSendMessageEnabled = ((ChatMemberRestricted) update.getMyChatMember().getNewChatMember()).getCanSendMessages();
                    log.debug("Изменение прав отправки ботом сообщений в группе {} на {}.", chatId, isSendMessageEnabled);
                    groupChatService.updateIsSendMessageEnabledByChatId(isSendMessageEnabled, chatId);
                    return;
                }
                log.debug("Поступил newChatMember бота в группе. Статус = {}.", status.name());
                Optional<GroupChat> groupChatOptional = groupChatService.find(chatId);
                if (groupChatOptional.isPresent()) {
                    groupChatService.updateMemberStatus(chatId, status);
                    if (MemberStatus.ADMINISTRATOR.equals(status)) {
                        responseSender.sendMessage(chatId, "Для того, чтобы узнать возможности бота, введите /help.");
                    }
                } else {
                    String title = update.getMyChatMember().getChat().getTitle();
                    log.debug("Зарегистрирована группа чат {}, статус бота {}, chat id {}.", title, status.name(), chatId);
                    groupChatService.register(chatId, update.getMyChatMember().getChat().getTitle(), status, GroupChatType.DEFAULT);
                }
            }
        } else if (update.hasMessage() && StringUtils.isNotEmpty(update.getMessage().getNewChatTitle())) {
            groupChatService.updateTitleByChatId(chatId, update.getMessage().getNewChatTitle());
        } else if (update.hasMessage()
                && update.getMessage().hasText()) {
            if (update.getMessage().isReply()
                    && update.getMessage().getReplyToMessage().getChatId().equals(chatId)
                    && update.getMessage().getText().equals("+")) {
                Matcher matcher = dealNumberPattern.matcher(update.getMessage().getReplyToMessage().getText());
                if (matcher.find()) {
                    String dealNumber = matcher.group(1);
                    String result = "Заявка №" + dealNumber + "\nОбработано.";
                    responseSender.sendEditedMessageText(chatId, update.getMessage().getReplyToMessage().getMessageId(),
                            result, null);
                } else {
                    responseSender.sendMessage(chatId, "Не получилось найти номер сделки в сообщении. Сообщение останется прежним.",
                            update.getMessage().getMessageId());
                }
            } else if (update.getMessage().getText().startsWith("/help")) {
                responseSender.sendMessage(chatId, "Чтобы отметить заявку обработанной, " +
                        "ответьте на сообщение бота с текстом \"+\". Текст будет заменен на следующий:\n" +
                        "<blockquote>Заявка №{номер заявки}.\nОбработано.</blockquote>", "html");
            }
        }
    }
}
