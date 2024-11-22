package tgb.btc.rce.sender;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.bot.RceBot;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.IMenuService;
import tgb.btc.rce.service.util.IMessagePropertiesService;
import tgb.btc.rce.vo.InlineButton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ResponseSender implements IResponseSender {

    private RceBot bot;

    private IReadUserService readUserService;

    private IMenuService menuService;

    private IKeyboardBuildService keyboardBuildService;

    private IMessagePropertiesService messagePropertiesService;

    private IUpdateService updateService;

    @Autowired
    public void setUpdateService(IUpdateService updateService) {
        this.updateService = updateService;
    }

    @Autowired
    public void setMessagePropertiesService(IMessagePropertiesService messagePropertiesService) {
        this.messagePropertiesService = messagePropertiesService;
    }

    @Autowired
    public void setKeyboardBuildService(IKeyboardBuildService keyboardBuildService) {
        this.keyboardBuildService = keyboardBuildService;
    }

    @Autowired
    public void setMenuService(IMenuService menuService) {
        this.menuService = menuService;
    }

    @Autowired
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
    }

    @Autowired
    public void setBot(RceBot bot) {
        this.bot = bot;
    }

    public Optional<Message> sendMessage(Long chatId, String text) {
        return Optional.ofNullable(executeSendMessage(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode("html")
                .build()));
    }

    @Override
    public Optional<Message> sendMessage(Long chatId, String text, Integer replyToMessageId) {
        return sendMessage(chatId, text, null, null, replyToMessageId);
    }

    public Optional<Message> sendMessageParseNode(Long chatId, String text, String parseNode) {
        return Optional.ofNullable(executeSendMessage(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode(parseNode)
                .build()));
    }


    public Optional<Message> sendMessageThrows(Long chatId, String text) throws TelegramApiException {
        return Optional.ofNullable(executeSendMessageThrows(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build()));
    }

    public Optional<Message> sendMessage(Long chatId, String text, Menu menu) {
        return sendMessage(chatId, text, menuService.build(menu, readUserService.getUserRoleByChatId(chatId)), null);
    }

    public Optional<Message> sendMessage(Long chatId, PropertiesMessage propertiesMessage, Menu menu) {
        return sendMessage(chatId, messagePropertiesService.getMessage(propertiesMessage), menu);
    }

    public Optional<Message> sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard) {
        return sendMessage(chatId, text, replyKeyboard, null);
    }

    public Optional<Message> sendMessage(Long chatId, String text, String parseMode) {
        return sendMessage(chatId, text, null, parseMode);
    }

    public Optional<Message> sendMessage(Long chatId, String text, InlineButton... inlineButtons) {
        return sendMessage(chatId, text, keyboardBuildService.buildInline(List.of(inlineButtons)));
    }

    @Override
    public Optional<Message> sendMessage(Long chatId, String text, String parseMode, InlineButton... inlineButtons) {
        return sendMessage(chatId, text, keyboardBuildService.buildInline(List.of(inlineButtons)), parseMode);
    }

    public Optional<Message> sendMessage(Long chatId, String text, List<InlineButton> buttons) {
        return sendMessage(chatId, text, keyboardBuildService.buildInline(buttons));
    }

    @Override
    public Optional<Message> sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard, String parseMode) {
        return sendMessage(chatId, text, replyKeyboard, parseMode, null);
    }

    @Override
    public Optional<Message> sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard, String parseMode, Integer replyToMessageId) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyToMessageId(replyToMessageId)
                .replyMarkup(replyKeyboard)
                .parseMode("html")
                .build();
        if (Strings.isNotEmpty(parseMode)) sendMessage.setParseMode(parseMode);
        return Optional.ofNullable(executeSendMessage(sendMessage));
    }

    @Override
    public Optional<Message> sendMessage(SendMessage sendMessage) {
        return Optional.ofNullable(executeSendMessage(sendMessage));
    }

    private Message executeSendMessage(SendMessage sendMessage) {
        try {
            return bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.warn("Не получилось отправить sendMessage: " + sendMessage, e);
            return null;
        }
    }

    private Message executeSendMessageThrows(SendMessage sendMessage) throws TelegramApiException {
        return bot.execute(sendMessage);
    }

    public Optional<Message> sendPhoto(Long chatId, String caption, String photo) {
        return sendPhoto(chatId, caption, photo, null);
    }

    public Optional<Message> sendPhoto(Long chatId, String caption, InputFile photo) {
        try {
            return Optional.of(bot.execute(SendPhoto.builder()
                    .chatId(chatId.toString())
                    .caption(caption)
                    .photo(photo)
                    .build()));
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить фото: chatId=" + chatId + ", caption=" + caption + ", photo=" + photo, e);
            return Optional.empty();
        }
    }

    public Optional<Message> sendPhoto(Long chatId, String caption, String photo, ReplyKeyboard replyKeyboard) {
        try {
            return Optional.of(bot.execute(SendPhoto.builder()
                    .chatId(chatId.toString())
                    .caption(caption)
                    .photo(new InputFile(photo))
                    .replyMarkup(replyKeyboard)
                    .parseMode("html")
                    .build()));
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить фото: chatId=" + chatId + ", caption=" + caption + ", photo=" + photo, e);
            return Optional.empty();
        }
    }

    @Override
    public Message sendAnimation(Long chatId, File file) {
        try {
            return bot.execute(SendAnimation.builder()
                    .chatId(chatId.toString())
                    .animation(new InputFile(file))
                    .build());
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить анимацию: chatId=" + chatId, e);
            return null;
        }
    }

    public Optional<Message> sendAnimation(Long chatId, String caption, String animation, ReplyKeyboard replyKeyboard) {
        try {
            return Optional.of(bot.execute(SendAnimation.builder()
                    .chatId(chatId.toString())
                    .caption(caption)
                    .animation(new InputFile(animation))
                    .replyMarkup(replyKeyboard)
                    .build()));
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить анимацию: chatId=" + chatId + ", caption="
                    + caption + ", animation=" + animation, e);
            return Optional.empty();
        }
    }

    public void deleteMessage(Long chatId, Integer messageId) {
        try {
            bot.execute(DeleteMessage.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .build());
        } catch (TelegramApiException ignored) {
        }
    }

    public void sendEditedMessageText(Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard) {
        try {
            bot.execute(EditMessageText.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .text(text)
                    .replyMarkup(keyboard)
                            .parseMode("html")
                    .build());
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить измененное сообщение: chatId" + chatId + ", text=" + text, e);
        }
    }

    @Override
    public void sendEditedMessageText(Long chatId, Integer messageId, String text) {
        sendEditedMessageText(chatId, messageId, text, null);
    }

    public void sendEditedMessageText(Long chatId, Integer messageId, String text, ReplyKeyboard replyKeyboard) {
        try {
            bot.execute(EditMessageText.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .text(text)
                    .parseMode("HTML")
                    .replyMarkup((InlineKeyboardMarkup) replyKeyboard)
                    .build());
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить измененное сообщение: chatId" + chatId + ", text=" + text, e);
        }
    }

    @Override
    public Message sendFile(Long chatId, File file) {
        try {
            return bot.execute(SendDocument.builder()
                    .chatId(chatId.toString())
                    .document(new InputFile(file))
                    .build());
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить файл: chatId=" + chatId + ", fileName=" + file.getName(), e);
            return null;
        }
    }

    @Override
    public Message sendFile(Long chatId, String caption, String fileId) {
        try {
            return bot.execute(SendDocument.builder()
                    .chatId(chatId.toString())
                    .caption(caption)
                    .document(new InputFile(fileId))
                    .build());
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить файл: chatId=" + chatId + ", fileId=" + fileId, e);
            return null;
        }
    }

    public Optional<org.telegram.telegrambots.meta.api.objects.File> execute(GetFile getFile) {
        try {
            return Optional.of(bot.execute(getFile));
        } catch (TelegramApiException e) {
            log.debug("Не получилось скачать файл:" + getFile.toString());
            return Optional.empty();
        }
    }

    public void downloadFile(String fileId, String localFilePath) {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        org.telegram.telegrambots.meta.api.objects.File file = execute(getFile)
                .orElseThrow(() -> new BaseException("Не получилось скачать файл " + fileId + " в " + localFilePath));
        java.io.File localFile = new java.io.File(localFilePath);
        try {
            InputStream is = new URL(file.getFileUrl(bot.getBotToken())).openStream();
            FileUtils.copyInputStreamToFile(is, localFile);
        } catch (Exception e) {
            throw new BaseException("Ошибки при скачивании файла.");
        }
    }

    public void downloadFile(Document document, String localFilePath) throws IOException {
        org.telegram.telegrambots.meta.api.objects.File file = getFilePath(document);

        java.io.File localFile = new java.io.File(localFilePath);
        InputStream is = new URL(file.getFileUrl(bot.getBotToken())).openStream();
        FileUtils.copyInputStreamToFile(is, localFile);
    }

    private org.telegram.telegrambots.meta.api.objects.File getFilePath(Document document) {
        GetFile getFile = new GetFile();
        getFile.setFileId(document.getFileId());
        // TODO проверить optional
        return execute(getFile).get();
    }

    @Override
    public void execute(AnswerInlineQuery answerInlineQuery) {
        try {
            bot.execute(answerInlineQuery);
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить answerInlineQuery: " + answerInlineQuery.toString(), e);
        }
    }

    @Override
    public Message execute(SendDice sendDice) {
        try {
            return bot.execute(sendDice);
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить sendDice: " + sendDice.toString(), e);
        }
        return null;
    }

    @Override
    public void sendMedia(Long chatId, List<InputMedia> media) {
        try {
            bot.execute(SendMediaGroup.builder()
                    .chatId(chatId.toString())
                    .medias(media)
                    .build());
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить медиа: chatId=" + chatId + ", media.size()=" + media.size());
        }
    }

    @Override
    public void sendInputFile(Long chatId, InputFile inputFile) {
        try {
            bot.execute(SendDocument.builder()
                    .chatId(chatId.toString())
                    .document(inputFile)
                    .build());
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить input file: chatId=" + chatId);
        }
    }

    @Override
    public boolean sendAnswerInlineQuery(String inlineQueryId, String title, String description, String messageText) {
        try {
            return bot.execute(AnswerInlineQuery.builder().inlineQueryId(inlineQueryId)
                    .result(InlineQueryResultArticle.builder()
                            .id(inlineQueryId)
                            .title(title)
                            .inputMessageContent(InputTextMessageContent.builder()
                                    .messageText(messageText)
                                    .build())
                            .description(description)
                            .build())
                    .build());
        } catch (TelegramApiException e) {
            log.trace("Не получилось отправить inlineQuery.");
            return false;
        }
    }

    @Override
    public boolean sendAnswerInlineQuery(String inlineQueryId, String title) {
        return sendAnswerInlineQuery(inlineQueryId, title, null, null);
    }

    @Override
    public void deleteCallbackMessageIfExists(Update update) {
        if (update.hasCallbackQuery())
            deleteMessage(updateService.getChatId(update), update.getCallbackQuery().getMessage().getMessageId());
    }

    @Override
    public void deleteCallbackMessageButtonsIfExists(Update update) {
        if (update.hasCallbackQuery()) {
            String text = update.getCallbackQuery().getMessage().getText();
            deleteMessage(updateService.getChatId(update), update.getCallbackQuery().getMessage().getMessageId());
            sendMessage(updateService.getChatId(update), text);
        }
    }

    @Override
    public void sendAnswerCallbackQuery(String callbackQueryId, String text, boolean showAlert) {
        try {
            bot.execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQueryId)
                    .text(text)
                    .showAlert(showAlert)
                    .build());
        } catch (TelegramApiException e) {
            log.error("Не получилось отправить answerCallbackQuery. text={}", text);
            log.error("Ошибка отправки answerCallbackQuery", e);
        }
    }

}
