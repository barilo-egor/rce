package tgb.btc.rce.service.handler.impl.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberRestricted;
import tgb.btc.api.web.INotificationsAPI;
import tgb.btc.library.bean.bot.GroupChat;
import tgb.btc.library.bean.web.api.ApiUser;
import tgb.btc.library.constants.enums.MemberStatus;
import tgb.btc.library.constants.enums.bot.GroupChatType;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.library.interfaces.service.bean.web.IApiUserService;
import tgb.btc.rce.enums.UpdateFilterType;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.handler.IUpdateFilter;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class GroupFilter implements IUpdateFilter {

    private final Pattern dealNumberPattern = Pattern.compile("№(\\d+)");

    private final IGroupChatService groupChatService;

    private final IResponseSender responseSender;

    private final INotificationsAPI notificationsAPI;

    private final IUpdateService updateService;

    private final IApiUserService apiUserService;

    private final String botUsername;

    public GroupFilter(IGroupChatService groupChatService, IResponseSender responseSender,
                       INotificationsAPI notificationsAPI,
                       IUpdateService updateService, IApiUserService apiUserService,
                       @Value("${bot.username}") String botUsername) {
        this.groupChatService = groupChatService;
        this.responseSender = responseSender;
        this.notificationsAPI = notificationsAPI;
        this.updateService = updateService;
        this.apiUserService = apiUserService;
        this.botUsername = botUsername;
    }

    @Override
    public void handle(Update update) {
        Long chatId = updateService.getGroupChatId(update);
        if (Objects.isNull(chatId)) {
            log.warn("Chat id группы не был найден. Update:{}", update);
            return;
        }
        UpdateType updateType = UpdateType.fromUpdate(update);
        if (UpdateType.MY_CHAT_MEMBER.equals(updateType)) {
            handleChatMember(update, chatId);
        } else if (UpdateType.MESSAGE.equals(updateType)) {
            handleMessage(update.getMessage(), chatId);
        }
    }

    private void handleMessage(Message message, Long chatId) {
        if (StringUtils.isNotEmpty(message.getNewChatTitle())) {
            groupChatService.updateTitleByChatId(chatId, message.getNewChatTitle());
        } else if (message.hasText()) {
            handleMessageText(message, chatId);
        }
    }

    private void handleChatMember(Update update, Long chatId) {
        ChatMember newChatMember = update.getMyChatMember().getNewChatMember();
        if (!botUsername.equals(newChatMember.getUser().getUserName())) return;
        MemberStatus status = MemberStatus.valueOf(newChatMember.getStatus().toUpperCase());
        if (MemberStatus.LEFT.equals(status) || MemberStatus.KICKED.equals(status)) {
            deleteGroup(chatId);
        } else if (MemberStatus.RESTRICTED.equals(status)) {
            updateIsSendMessageEnabled(update.getMyChatMember().getNewChatMember(), chatId);
        } else {
            registerOrUpdateGroup(status, update.getMyChatMember().getChat(), chatId);
        }
    }

    private void registerOrUpdateGroup(MemberStatus status, Chat chat, Long chatId) {
        log.debug("Поступил newChatMember бота в группе. Статус = {}.", status.name());
        Optional<GroupChat> groupChatOptional = groupChatService.find(chatId);
        if (groupChatOptional.isPresent()) {
            groupChatService.updateMemberStatus(chatId, status);
        } else {
            String title = chat.getTitle();
            log.debug("Зарегистрирована группа чат {}, статус бота {}, chat id {}.", title, status.name(), chatId);
            groupChatService.register(chatId, title, status, GroupChatType.DEFAULT);
        }
    }

    private void updateIsSendMessageEnabled(ChatMember chatMember, Long chatId) {
        boolean isSendMessageEnabled = ((ChatMemberRestricted) chatMember).getCanSendMessages();
        log.debug("Изменение прав отправки ботом сообщений в группе {} на {}.", chatId, isSendMessageEnabled);
        groupChatService.updateIsSendMessageEnabledByChatId(isSendMessageEnabled, chatId);
    }

    private void deleteGroup(Long chatId) {
        log.debug("Бот был удален из группы chatid={}", chatId);
        boolean isDealRequestGroup = false;
        Optional<GroupChat> optionalGroupChat = groupChatService.getAllByType(GroupChatType.DEAL_REQUEST).stream().findAny();
        if (optionalGroupChat.isPresent()) {
            isDealRequestGroup = optionalGroupChat.get().getChatId().equals(chatId);
        } else {
            ApiUser apiUser = apiUserService.getByGroupChatId(chatId);
            if (Objects.nonNull(apiUser)) {
                apiUser.setGroupChat(null);
                apiUserService.save(apiUser);
            }
        }
        groupChatService.deleteIfExistsByChatId(chatId);
        if (isDealRequestGroup) notificationsAPI.notifyDeletedDealRequestGroup();
    }

    private void handleMessageText(Message message, Long chatId) {
        if (!groupChatService.isDealRequest(chatId))
            return;
        if (isHandledDealRequest(message, chatId)) {
            Matcher matcher = dealNumberPattern.matcher(message.getReplyToMessage().getText());
            if (matcher.find()) {
                String dealNumber = matcher.group(1);
                String result = "Заявка №" + dealNumber + "\nОбработано.";
                responseSender.sendEditedMessageText(chatId, message.getReplyToMessage().getMessageId(),
                        result);
            } else {
                responseSender.sendMessage(chatId, "Не получилось найти номер сделки в сообщении. Сообщение останется прежним.",
                        message.getMessageId());
            }
        } else if (message.getText().startsWith("/help")) {
            responseSender.sendMessage(chatId, """
                        Чтобы отметить заявку обработанной, \
                        ответьте на сообщение бота с текстом "+". Текст будет заменен на следующий:
                        <blockquote>Заявка №{номер заявки}.
                        Обработано.</blockquote>""", "html");
        }
    }

    private boolean isHandledDealRequest(Message message, Long chatId) {
        return message.isReply()
                && message.getReplyToMessage().getChatId().equals(chatId)
                && message.getText().equals("+");
    }

    @Override
    public UpdateFilterType getType() {
        return UpdateFilterType.GROUP;
    }
}
