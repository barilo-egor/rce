package tgb.btc.rce.bot;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.util.BotPropertiesUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@Service
@Slf4j
public class RceBot extends TelegramLongPollingBot {

    private final IUpdateDispatcher updateDispatcher;

    @Autowired
    public RceBot(IUpdateDispatcher updateDispatcher) {
        this.updateDispatcher = updateDispatcher;
    }

    @Override
    public String getBotUsername() {
        return BotPropertiesUtil.getUsername();
    }

    @Override
    public String getBotToken() {
        return BotPropertiesUtil.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            updateDispatcher.dispatch(update);
        } catch (Exception e) {
            try {
                execute(SendMessage.builder()
                        .chatId(UpdateUtil.getChatId(update).toString())
                        .text("Что-то пошло не так: " + e.getMessage() + "\n" + ExceptionUtils.getFullStackTrace(e))
                        .build());
                log.error("Ошибка", e);
            } catch (TelegramApiException ignored) {
            }
        }
    }
}
