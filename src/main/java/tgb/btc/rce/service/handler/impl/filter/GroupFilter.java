package tgb.btc.rce.service.handler.impl.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
        if (update.hasMyChatMember()) {
            ChatMember newChatMember = update.getMyChatMember().getNewChatMember();
            if (botUsername.equals(newChatMember.getUser().getUserName())) {
                MemberStatus status = MemberStatus.valueOf(newChatMember.getStatus().toUpperCase());
                if (MemberStatus.LEFT.equals(status) || MemberStatus.KICKED.equals(status)) {
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
                } else {
                    String title = update.getMyChatMember().getChat().getTitle();
                    log.debug("Зарегистрирована группа чат {}, статус бота {}, chat id {}.", title, status.name(), chatId);
                    groupChatService.register(chatId, title, status, GroupChatType.DEFAULT);
                }
            }
        } else if (update.hasMessage() && StringUtils.isNotEmpty(update.getMessage().getNewChatTitle())) {
            groupChatService.updateTitleByChatId(chatId, update.getMessage().getNewChatTitle());
        } else if (update.hasMessage()
                && update.getMessage().hasText()) {
            if (!groupChatService.isDealRequest(chatId))
                return;
            if (update.getMessage().isReply()
                    && update.getMessage().getReplyToMessage().getChatId().equals(chatId)
                    && update.getMessage().getText().equals("+")) {
                Matcher matcher = dealNumberPattern.matcher(update.getMessage().getReplyToMessage().getText());
                if (matcher.find()) {
                    String dealNumber = matcher.group(1);
                    String result = "Заявка №" + dealNumber + "\nОбработано.";
                    responseSender.sendEditedMessageText(chatId, update.getMessage().getReplyToMessage().getMessageId(),
                            result);
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

    @Override
    public UpdateFilterType getType() {
        return UpdateFilterType.GROUP;
    }
}
