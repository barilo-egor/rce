package tgb.btc.rce.service.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.service.IUpdateService;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class UpdateService implements IUpdateService {

    @Override
    public Long getChatId(Update update) {
        return getFrom(update).getId();
    }

    @Override
    public Long getGroupChatId(Update update) {
        if (update.hasMyChatMember())
            return update.getMyChatMember().getChat().getId();
        if (update.hasChatMember())
            return update.getChatMember().getChat().getId();
        if (update.hasMessage())
            return update.getMessage().getChat().getId();
        else return null;
    }

    @Override
    public boolean isGroupMessage(Update update) {
        return (update.hasMyChatMember() &&
                (update.getMyChatMember().getChat().isGroupChat() || update.getMyChatMember().getChat().isSuperGroupChat()))
                || (update.hasMessage()
                && (update.getMessage().getChat().isGroupChat() || update.getMessage().getChat().isSuperGroupChat()));
    }

    @Override
    public String getUsername(Update update) {
        return getFrom(update).getUserName();
    }

    private User getFrom(Update update) {
        switch (UpdateType.fromUpdate(update)) {
            case MESSAGE:
                return update.getMessage().getFrom();
            case CALLBACK_QUERY:
                return update.getCallbackQuery().getFrom();
            case INLINE_QUERY:
                return update.getInlineQuery().getFrom();
            case MY_CHAT_MEMBER:
                return update.getMyChatMember().getFrom();
            default:
                throw new BaseException("Тип апдейта не найден: " + update);
        }
    }

    @Override
    public Integer getMessageId(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getMessageId();
        }
        if (Objects.isNull(update.getMessage()))
            throw new BaseException("Невозможно получить message id, т.к. message==null.");
        return update.getMessage().getMessageId();
    }

    @Override
    public boolean hasMessageText(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    @Override
    public String getMessageText(Update update) {
        if (Objects.isNull(update.getMessage()))
            throw new BaseException("Невозможно получить message text, т.к. message==null.");
        return update.getMessage().getText();
    }

    @Override
    public Long getLongFromText(Update update) {
        return Long.parseLong(getMessageText(update));
    }

    @Override
    public BigDecimal getBigDecimalFromText(Update update) {
        return BigDecimal.valueOf(Double.parseDouble(getMessageText(update).replace(",", ".")));
    }

    @Override
    public Message getMessage(Update update) {
        return switch (UpdateType.fromUpdate(update)) {
            case MESSAGE -> update.getMessage();
            case CALLBACK_QUERY -> update.getCallbackQuery().getMessage();
            default -> throw new BaseException("Не найден тип апдейта для получения месседжа.");
        };
    }

    @Override
    public boolean hasDocumentOrPhoto(Update update) {
        return hasDocument(update) || hasPhoto(update);
    }

    public boolean hasDocument(Update update) {
        return update.hasMessage() && update.getMessage().hasDocument();
    }

    public boolean hasPhoto(Update update) {
        return update.hasMessage() && update.getMessage().hasPhoto();
    }
}
