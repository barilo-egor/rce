package tgb.btc.rce.sender;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.rce.bot.RceBot;
import tgb.btc.rce.service.impl.keyboard.KeyboardBuildService;
import tgb.btc.rce.vo.InlineButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResponseSenderTest {

    @Mock
    private RceBot bot;

    @Mock
    private KeyboardBuildService keyboardBuildService;

    @InjectMocks
    private ResponseSender responseSender;

    @Test
    void sendMessageText() throws TelegramApiException {
        String chatId = "12345678";
        String text = "message text";
        responseSender.sendMessage(Long.valueOf(chatId), text);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(argumentCaptor.capture());
        SendMessage value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertNull(value.getMessageThreadId()),
                () -> assertEquals(text, value.getText()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertNull(value.getDisableNotification()),
                () -> assertNull(value.getReplyToMessageId()),
                () -> assertNull(value.getReplyMarkup()),
                () -> assertNull(value.getEntities()),
                () -> assertNull(value.getAllowSendingWithoutReply()),
                () -> assertNull(value.getProtectContent()),
                () -> assertEquals("sendmessage", value.getMethod()),
                () -> assertNull(value.getDisableWebPagePreview())
        );
    }

    @Test
    void sendMessageTextReply() throws TelegramApiException {
        String chatId = "12345678";
        String text = "message text";
        Integer replyMessageId = 55000;
        responseSender.sendMessage(Long.valueOf(chatId), text, replyMessageId);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(argumentCaptor.capture());
        SendMessage value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertNull(value.getMessageThreadId()),
                () -> assertEquals(text, value.getText()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertNull(value.getDisableNotification()),
                () -> assertEquals(replyMessageId, value.getReplyToMessageId()),
                () -> assertNull(value.getReplyMarkup()),
                () -> assertNull(value.getEntities()),
                () -> assertNull(value.getAllowSendingWithoutReply()),
                () -> assertNull(value.getProtectContent()),
                () -> assertEquals("sendmessage", value.getMethod()),
                () -> assertNull(value.getDisableWebPagePreview())
        );
    }

    @Test
    void sendMessageTextWithKeyboard() throws TelegramApiException {
        String chatId = "12345678";
        String text = "message text";
        ReplyKeyboard replyKeyboard = new InlineKeyboardMarkup();
        responseSender.sendMessage(Long.valueOf(chatId), text, replyKeyboard);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(argumentCaptor.capture());
        SendMessage value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertNull(value.getMessageThreadId()),
                () -> assertEquals(text, value.getText()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertNull(value.getDisableNotification()),
                () -> assertNull(value.getReplyToMessageId()),
                () -> assertEquals(replyKeyboard, value.getReplyMarkup()),
                () -> assertNull(value.getEntities()),
                () -> assertNull(value.getAllowSendingWithoutReply()),
                () -> assertNull(value.getProtectContent()),
                () -> assertEquals("sendmessage", value.getMethod()),
                () -> assertNull(value.getDisableWebPagePreview())
        );
    }

    @Test
    void sendMessageTextWithInlineButtons() throws TelegramApiException {
        String chatId = "12345678";
        String text = "message text";
        InlineButton[] inlineButtons = new InlineButton[] {InlineButton.builder().build(), InlineButton.builder().build()};
        InlineKeyboardMarkup replyKeyboard = new InlineKeyboardMarkup();
        when(keyboardBuildService.buildInline(anyList())).thenReturn(replyKeyboard);

        responseSender.sendMessage(Long.valueOf(chatId), text, inlineButtons);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(argumentCaptor.capture());

        SendMessage value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertNull(value.getMessageThreadId()),
                () -> assertEquals(text, value.getText()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertNull(value.getDisableNotification()),
                () -> assertNull(value.getReplyToMessageId()),
                () -> assertEquals(replyKeyboard, value.getReplyMarkup()),
                () -> assertNull(value.getEntities()),
                () -> assertNull(value.getAllowSendingWithoutReply()),
                () -> assertNull(value.getProtectContent()),
                () -> assertEquals("sendmessage", value.getMethod()),
                () -> assertNull(value.getDisableWebPagePreview())
        );
    }

    @Test
    void sendMessageTextWithListInlineButtons() throws TelegramApiException {
        String chatId = "12345678";
        String text = "message text";
        List<InlineButton> inlineButtons = List.of(InlineButton.builder().build(), InlineButton.builder().build());
        InlineKeyboardMarkup replyKeyboard = new InlineKeyboardMarkup();
        when(keyboardBuildService.buildInline(inlineButtons)).thenReturn(replyKeyboard);

        responseSender.sendMessage(Long.valueOf(chatId), text, inlineButtons);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(argumentCaptor.capture());

        SendMessage value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertNull(value.getMessageThreadId()),
                () -> assertEquals(text, value.getText()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertNull(value.getDisableNotification()),
                () -> assertNull(value.getReplyToMessageId()),
                () -> assertEquals(replyKeyboard, value.getReplyMarkup()),
                () -> assertNull(value.getEntities()),
                () -> assertNull(value.getAllowSendingWithoutReply()),
                () -> assertNull(value.getProtectContent()),
                () -> assertEquals("sendmessage", value.getMethod()),
                () -> assertNull(value.getDisableWebPagePreview())
        );
    }

    @Test
    void sendMessageReturnNullIfTelegramApiException() throws TelegramApiException {
        when(bot.execute((SendMessage) any())).thenThrow(TelegramApiException.class);
        assertEquals(Optional.empty(), responseSender.sendMessage(123456789L, "text"));
    }

    @Test
    void sendMessageThrowsSendMessage() throws TelegramApiException {
        Long chatId = 123456789L;
        String text = "message text";
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        responseSender.sendMessageThrows(chatId, text);
        verify(bot, times(1)).execute(argumentCaptor.capture());
        SendMessage value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId.toString(), value.getChatId()),
                () -> assertNull(value.getMessageThreadId()),
                () -> assertEquals(text, value.getText()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertNull(value.getDisableNotification()),
                () -> assertNull(value.getReplyToMessageId()),
                () -> assertNull(value.getReplyMarkup()),
                () -> assertNull(value.getEntities()),
                () -> assertNull(value.getAllowSendingWithoutReply()),
                () -> assertNull(value.getProtectContent()),
                () -> assertEquals("sendmessage", value.getMethod()),
                () -> assertNull(value.getDisableWebPagePreview())
        );
    }

    @Test
    void sendPhotoInputFileWithCaption() throws TelegramApiException {
        String chatId = "12345678";
        String caption = "message text";
        InputFile photo = new InputFile();

        responseSender.sendPhoto(Long.valueOf(chatId), caption, photo);
        ArgumentCaptor<SendPhoto> argumentCaptor = ArgumentCaptor.forClass(SendPhoto.class);
        verify(bot).execute(argumentCaptor.capture());

        SendPhoto value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertEquals(photo, value.getPhoto()),
                () -> assertNull(value.getMessageThreadId()),
                () -> assertEquals(caption, value.getCaption()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertNull(value.getDisableNotification()),
                () -> assertNull(value.getReplyToMessageId()),
                () -> assertNull(value.getReplyMarkup()),
                () -> assertEquals(0, value.getCaptionEntities().size()),
                () -> assertNull(value.getAllowSendingWithoutReply()),
                () -> assertNull(value.getProtectContent()),
                () -> assertEquals("sendphoto", value.getMethod())
        );
    }

    @Test
    void sendPhotoByFileIdWithCaptionAndKeyboard() throws TelegramApiException {
        String chatId = "12345678";
        String caption = "message text";
        String fileId = "CgACAgIAAxkDAAIS42c7KceBwAUkHv6iuwABcJqHcm4nkgACXWUAAp882UlT-gJmcdZcGTYE";
        ReplyKeyboard replyKeyboard = new InlineKeyboardMarkup();

        responseSender.sendPhoto(Long.valueOf(chatId), caption, fileId, replyKeyboard);
        ArgumentCaptor<SendPhoto> argumentCaptor = ArgumentCaptor.forClass(SendPhoto.class);
        verify(bot).execute(argumentCaptor.capture());

        SendPhoto value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertEquals(new InputFile(fileId), value.getPhoto()),
                () -> assertNull(value.getMessageThreadId()),
                () -> assertEquals(caption, value.getCaption()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertNull(value.getDisableNotification()),
                () -> assertNull(value.getReplyToMessageId()),
                () -> assertEquals(replyKeyboard, value.getReplyMarkup()),
                () -> assertEquals(0, value.getCaptionEntities().size()),
                () -> assertNull(value.getAllowSendingWithoutReply()),
                () -> assertNull(value.getProtectContent()),
                () -> assertEquals("sendphoto", value.getMethod())
        );
    }

    @Test
    void sendPhotoByFileIdWithCaption() throws TelegramApiException {
        String chatId = "12345678";
        String caption = "message text";
        String fileId = "CgACAgIAAxkDAAIS42c7KceBwAUkHv6iuwABcJqHcm4nkgACXWUAAp882UlT-gJmcdZcGTYE";

        responseSender.sendPhoto(Long.valueOf(chatId), caption, fileId);
        ArgumentCaptor<SendPhoto> argumentCaptor = ArgumentCaptor.forClass(SendPhoto.class);
        verify(bot).execute(argumentCaptor.capture());

        SendPhoto value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertEquals(new InputFile(fileId), value.getPhoto()),
                () -> assertNull(value.getMessageThreadId()),
                () -> assertEquals(caption, value.getCaption()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertNull(value.getDisableNotification()),
                () -> assertNull(value.getReplyToMessageId()),
                () -> assertNull(value.getReplyMarkup()),
                () -> assertEquals(0, value.getCaptionEntities().size()),
                () -> assertNull(value.getAllowSendingWithoutReply()),
                () -> assertNull(value.getProtectContent()),
                () -> assertEquals("sendphoto", value.getMethod())
        );
    }

    @Test
    void sendPhotoInputFileWithCaptionAndKeyboard() throws TelegramApiException {
        String chatId = "12345678";
        String caption = "message text";
        InputFile inputFile = new InputFile("CgACAgIAAxkDAAIS42c7KceBwAUkHv6iuwABcJqHcm4nkgACXWUAAp882UlT-gJmcdZcGTYE");
        ReplyKeyboard replyKeyboard = new InlineKeyboardMarkup();

        responseSender.sendPhoto(Long.valueOf(chatId), caption, inputFile, replyKeyboard);
        ArgumentCaptor<SendPhoto> argumentCaptor = ArgumentCaptor.forClass(SendPhoto.class);
        verify(bot).execute(argumentCaptor.capture());

        SendPhoto value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertEquals(inputFile, value.getPhoto()),
                () -> assertNull(value.getMessageThreadId()),
                () -> assertEquals(caption, value.getCaption()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertNull(value.getDisableNotification()),
                () -> assertNull(value.getReplyToMessageId()),
                () -> assertEquals(replyKeyboard, value.getReplyMarkup()),
                () -> assertEquals(0, value.getCaptionEntities().size()),
                () -> assertNull(value.getAllowSendingWithoutReply()),
                () -> assertNull(value.getProtectContent()),
                () -> assertEquals("sendphoto", value.getMethod())
        );
    }

    @Test
    void sendPhotoReturnNullIfTelegramApiException() throws TelegramApiException {
        when(bot.execute((SendPhoto) any())).thenThrow(TelegramApiException.class);
        assertEquals(Optional.empty(),
                responseSender.sendPhoto(123456789L, "caption", new InputFile("photo"), new InlineKeyboardMarkup()));
    }

    @Test
    void sendAnimation() throws TelegramApiException {
        String chatId = "12345678";
        File file = new File("file");
        
        responseSender.sendAnimation(Long.valueOf(chatId), file);
        ArgumentCaptor<SendAnimation> argumentCaptor = ArgumentCaptor.forClass(SendAnimation.class);
        verify(bot).execute(argumentCaptor.capture());

        SendAnimation value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertEquals(new InputFile(file), value.getAnimation()),
                () -> assertNull(value.getMessageThreadId()),
                () -> assertNull(value.getCaption()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertNull(value.getDisableNotification()),
                () -> assertNull(value.getReplyToMessageId()),
                () -> assertNull(value.getReplyMarkup()),
                () -> assertEquals(0, value.getCaptionEntities().size()),
                () -> assertNull(value.getAllowSendingWithoutReply()),
                () -> assertNull(value.getProtectContent()),
                () -> assertEquals("sendAnimation", value.getMethod())
        );
    }

    @Test
    void sendAnimationWithCaptionAndKeyboard() throws TelegramApiException {
        String chatId = "12345678";
        String caption = "message text";
        ReplyKeyboard replyKeyboard = new InlineKeyboardMarkup();
        String fileId = "CgACAgIAAxkDAAIS42c7KceBwAUkHv6iuwABcJqHcm4nkgACXWUAAp882UlT-gJmcdZcGTYE";

        responseSender.sendAnimation(Long.valueOf(chatId), caption, fileId, replyKeyboard);
        ArgumentCaptor<SendAnimation> argumentCaptor = ArgumentCaptor.forClass(SendAnimation.class);
        verify(bot).execute(argumentCaptor.capture());

        SendAnimation value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertEquals(new InputFile(fileId), value.getAnimation()),
                () -> assertNull(value.getMessageThreadId()),
                () -> assertEquals(caption, value.getCaption()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertNull(value.getDisableNotification()),
                () -> assertNull(value.getReplyToMessageId()),
                () -> assertEquals(replyKeyboard, value.getReplyMarkup()),
                () -> assertEquals(0, value.getCaptionEntities().size()),
                () -> assertNull(value.getAllowSendingWithoutReply()),
                () -> assertNull(value.getProtectContent()),
                () -> assertEquals("sendAnimation", value.getMethod())
        );
    }

    @Test
    void sendAnimationInputFileWithCaptionAndKeyboard() throws TelegramApiException {
        String chatId = "12345678";
        String caption = "message text";
        ReplyKeyboard replyKeyboard = new InlineKeyboardMarkup();
        InputFile inputFile = new InputFile("CgACAgIAAxkDAAIS42c7KceBwAUkHv6iuwABcJqHcm4nkgACXWUAAp882UlT-gJmcdZcGTYE");

        responseSender.sendAnimation(Long.valueOf(chatId), inputFile, caption, replyKeyboard);
        ArgumentCaptor<SendAnimation> argumentCaptor = ArgumentCaptor.forClass(SendAnimation.class);
        verify(bot).execute(argumentCaptor.capture());

        SendAnimation value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertEquals(inputFile, value.getAnimation()),
                () -> assertNull(value.getMessageThreadId()),
                () -> assertEquals(caption, value.getCaption()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertNull(value.getDisableNotification()),
                () -> assertNull(value.getReplyToMessageId()),
                () -> assertEquals(replyKeyboard, value.getReplyMarkup()),
                () -> assertEquals(0, value.getCaptionEntities().size()),
                () -> assertNull(value.getAllowSendingWithoutReply()),
                () -> assertNull(value.getProtectContent()),
                () -> assertEquals("sendAnimation", value.getMethod())
        );
    }

    @Test
    void sendAnimationReturnNullIfTelegramApiException() throws TelegramApiException {
        when(bot.execute((SendAnimation) any())).thenThrow(TelegramApiException.class);
        assertNull(responseSender.sendAnimation(123456789L,
                new InputFile("animation"), "caption", new InlineKeyboardMarkup()));
    }

    @Test
    void deleteMessage() throws TelegramApiException {
        String chatId = "123456789";
        Integer messageId = 54500;
        responseSender.deleteMessage(Long.valueOf(chatId), messageId);
        ArgumentCaptor<DeleteMessage> argumentCaptor = ArgumentCaptor.forClass(DeleteMessage.class);
        verify(bot, times(1)).execute(argumentCaptor.capture());
        DeleteMessage value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertEquals(messageId, value.getMessageId()),
                () -> assertEquals("deleteMessage", value.getMethod())
        );
    }

    @Test
    void sendEditedMessageText() throws TelegramApiException {
        String chatId = "12345678";
        Integer messageId = 54500;
        String text = "message text";

        responseSender.sendEditedMessageText(Long.valueOf(chatId), messageId, text);
        ArgumentCaptor<EditMessageText> argumentCaptor = ArgumentCaptor.forClass(EditMessageText.class);
        verify(bot).execute(argumentCaptor.capture());

        EditMessageText value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertEquals(messageId, value.getMessageId()),
                () -> assertEquals(text, value.getText()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertNull(value.getReplyMarkup()),
                () -> assertNull(value.getEntities()),
                () -> assertNull(value.getDisableWebPagePreview()),
                () -> assertNull(value.getInlineMessageId()),
                () -> assertEquals("editmessagetext", value.getMethod())
        );
    }

    @Test
    void sendEditedMessageTextWithButtons() throws TelegramApiException {
        String chatId = "12345678";
        Integer messageId = 54500;
        String text = "message text";
        List<InlineButton> inlineButtons = List.of(InlineButton.builder().build(), InlineButton.builder().build());
        InlineKeyboardMarkup replyKeyboard = new InlineKeyboardMarkup();
        when(keyboardBuildService.buildInline(inlineButtons)).thenReturn(replyKeyboard);

        responseSender.sendEditedMessageText(Long.valueOf(chatId), messageId, text, inlineButtons);
        ArgumentCaptor<EditMessageText> argumentCaptor = ArgumentCaptor.forClass(EditMessageText.class);
        verify(bot).execute(argumentCaptor.capture());

        EditMessageText value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertEquals(messageId, value.getMessageId()),
                () -> assertEquals(text, value.getText()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertEquals(replyKeyboard, value.getReplyMarkup()),
                () -> assertNull(value.getEntities()),
                () -> assertNull(value.getDisableWebPagePreview()),
                () -> assertNull(value.getInlineMessageId()),
                () -> assertEquals("editmessagetext", value.getMethod())
        );
    }

    @Test
    void sendEditedMessageTextWithReplyKeyboard() throws TelegramApiException {
        String chatId = "12345678";
        Integer messageId = 54500;
        String text = "message text";
        ReplyKeyboard replyKeyboard = new InlineKeyboardMarkup();

        responseSender.sendEditedMessageText(Long.valueOf(chatId), messageId, text, replyKeyboard);
        ArgumentCaptor<EditMessageText> argumentCaptor = ArgumentCaptor.forClass(EditMessageText.class);
        verify(bot).execute(argumentCaptor.capture());

        EditMessageText value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertEquals(messageId, value.getMessageId()),
                () -> assertEquals(text, value.getText()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertEquals(replyKeyboard, value.getReplyMarkup()),
                () -> assertNull(value.getEntities()),
                () -> assertNull(value.getDisableWebPagePreview()),
                () -> assertNull(value.getInlineMessageId()),
                () -> assertEquals("editmessagetext", value.getMethod())
        );
    }

    @Test
    void sendEditedMessageTextWithInlineKeyboardMarkup() throws TelegramApiException {
        String chatId = "12345678";
        Integer messageId = 54500;
        String text = "message text";
        InlineKeyboardMarkup replyKeyboard = new InlineKeyboardMarkup();

        responseSender.sendEditedMessageText(Long.valueOf(chatId), messageId, text, replyKeyboard);
        ArgumentCaptor<EditMessageText> argumentCaptor = ArgumentCaptor.forClass(EditMessageText.class);
        verify(bot).execute(argumentCaptor.capture());

        EditMessageText value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertEquals(messageId, value.getMessageId()),
                () -> assertEquals(text, value.getText()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertEquals(replyKeyboard, value.getReplyMarkup()),
                () -> assertNull(value.getEntities()),
                () -> assertNull(value.getDisableWebPagePreview()),
                () -> assertNull(value.getInlineMessageId()),
                () -> assertEquals("editmessagetext", value.getMethod())
        );
    }

    @Test
    void sendFileWithCaptionByFileId() throws TelegramApiException {
        String chatId = "12345678";
        String caption = "message text";
        String fileId = "CgACAgIAAxkDAAIS42c7KceBwAUkHv6iuwABcJqHcm4nkgACXWUAAp882UlT-gJmcdZcGTYE";

        responseSender.sendFile(Long.valueOf(chatId), caption, fileId);
        ArgumentCaptor<SendDocument> argumentCaptor = ArgumentCaptor.forClass(SendDocument.class);
        verify(bot).execute(argumentCaptor.capture());

        SendDocument value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertEquals(caption, value.getCaption()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertEquals("senddocument", value.getMethod()),
                () -> assertEquals(new InputFile(fileId), value.getDocument()),
                () -> assertNull(value.getReplyMarkup()),
                () -> assertNull(value.getThumbnail()),
                () -> assertEquals(new ArrayList<>(), value.getCaptionEntities()),
                () -> assertNull(value.getAllowSendingWithoutReply()),
                () -> assertNull(value.getDisableNotification()),
                () -> assertNull(value.getDisableContentTypeDetection()),
                () -> assertEquals(new InputFile(fileId), value.getFile()),
                () -> assertEquals("document", value.getFileField()),
                () -> assertNull(value.getMessageThreadId()),
                () -> assertNull(value.getReplyToMessageId())
        );
    }

    @Test
    void sendFile() throws TelegramApiException {
        String chatId = "12345678";
        File file = new File("file");

        responseSender.sendFile(Long.valueOf(chatId), file);
        ArgumentCaptor<SendDocument> argumentCaptor = ArgumentCaptor.forClass(SendDocument.class);
        verify(bot).execute(argumentCaptor.capture());

        SendDocument value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertNull(value.getCaption()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertEquals("senddocument", value.getMethod()),
                () -> assertEquals(new InputFile(file), value.getDocument()),
                () -> assertNull(value.getReplyMarkup()),
                () -> assertNull(value.getThumbnail()),
                () -> assertEquals(new ArrayList<>(), value.getCaptionEntities()),
                () -> assertNull(value.getAllowSendingWithoutReply()),
                () -> assertNull(value.getDisableNotification()),
                () -> assertNull(value.getDisableContentTypeDetection()),
                () -> assertEquals(new InputFile(file), value.getFile()),
                () -> assertEquals("document", value.getFileField()),
                () -> assertNull(value.getMessageThreadId()),
                () -> assertNull(value.getReplyToMessageId())
        );
    }

    @Test
    void sendFileInputFile() throws TelegramApiException {
        String chatId = "12345678";
        InputFile inputFile = new InputFile("file");

        responseSender.sendFile(Long.valueOf(chatId), inputFile);
        ArgumentCaptor<SendDocument> argumentCaptor = ArgumentCaptor.forClass(SendDocument.class);
        verify(bot).execute(argumentCaptor.capture());

        SendDocument value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertNull(value.getCaption()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertEquals("senddocument", value.getMethod()),
                () -> assertEquals(inputFile, value.getDocument()),
                () -> assertNull(value.getReplyMarkup()),
                () -> assertNull(value.getThumbnail()),
                () -> assertEquals(new ArrayList<>(), value.getCaptionEntities()),
                () -> assertNull(value.getAllowSendingWithoutReply()),
                () -> assertNull(value.getDisableNotification()),
                () -> assertNull(value.getDisableContentTypeDetection()),
                () -> assertEquals(inputFile, value.getFile()),
                () -> assertEquals("document", value.getFileField()),
                () -> assertNull(value.getMessageThreadId()),
                () -> assertNull(value.getReplyToMessageId())
        );
    }

    @Test
    void sendFileInputFileAndCaption() throws TelegramApiException {
        String chatId = "12345678";
        InputFile inputFile = new InputFile("file");
        String caption = "caption";

        responseSender.sendFile(Long.valueOf(chatId), inputFile, caption);
        ArgumentCaptor<SendDocument> argumentCaptor = ArgumentCaptor.forClass(SendDocument.class);
        verify(bot).execute(argumentCaptor.capture());

        SendDocument value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId, value.getChatId()),
                () -> assertEquals(caption, value.getCaption()),
                () -> assertEquals("html", value.getParseMode()),
                () -> assertEquals("senddocument", value.getMethod()),
                () -> assertEquals(inputFile, value.getDocument()),
                () -> assertNull(value.getReplyMarkup()),
                () -> assertNull(value.getThumbnail()),
                () -> assertEquals(new ArrayList<>(), value.getCaptionEntities()),
                () -> assertNull(value.getAllowSendingWithoutReply()),
                () -> assertNull(value.getDisableNotification()),
                () -> assertNull(value.getDisableContentTypeDetection()),
                () -> assertEquals(inputFile, value.getFile()),
                () -> assertEquals("document", value.getFileField()),
                () -> assertNull(value.getMessageThreadId()),
                () -> assertNull(value.getReplyToMessageId())
        );
    }

    @Test
    void sendFileReturnNullIfTelegramApiException() throws TelegramApiException {
        when(bot.execute((SendDocument) any())).thenThrow(TelegramApiException.class);
        assertNull(responseSender.sendFile(123456789L, new InputFile("animation"), "caption"));
    }

    @Test
    void sendAnswerInlineQuery() throws TelegramApiException {
        String inlineQueryId = "12356";
        String title = "title";
        String description = "description";
        String messageText = "messageText";

        responseSender.sendAnswerInlineQuery(inlineQueryId, title, description, messageText);
        ArgumentCaptor<AnswerInlineQuery> argumentCaptor = ArgumentCaptor.forClass(AnswerInlineQuery.class);
        verify(bot).execute(argumentCaptor.capture());

        AnswerInlineQuery value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(inlineQueryId, value.getInlineQueryId()),
                () -> assertEquals(title, ((InlineQueryResultArticle) value.getResults().get(0)).getTitle()),
                () -> assertEquals(description, ((InlineQueryResultArticle) value.getResults().get(0)).getDescription()),
                () -> assertEquals(messageText, ((InputTextMessageContent) ((InlineQueryResultArticle) value.getResults().get(0)).getInputMessageContent()).getMessageText())
        );
    }

    @Test
    void deleteCallbackMessageIfExistsShouldSkipWithoutCallback() throws TelegramApiException {
        Update update = new Update();
        responseSender.deleteCallbackMessageIfExists(update);
        verify(bot, times(0)).execute((DeleteMessage) any());
    }

    @Test
    void deleteCallbackMessage() throws TelegramApiException {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        Integer messageId = 12345;
        message.setMessageId(messageId);
        callbackQuery.setMessage(message);
        User user = new User();
        Long chatId = 123456789L;
        user.setId(chatId);
        callbackQuery.setFrom(user);
        update.setCallbackQuery(callbackQuery);
        responseSender.deleteCallbackMessageIfExists(update);
        ArgumentCaptor<DeleteMessage> argumentCaptor = ArgumentCaptor.forClass(DeleteMessage.class);
        verify(bot, times(1)).execute(argumentCaptor.capture());
        DeleteMessage value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(chatId.toString(), value.getChatId()),
                () -> assertEquals(messageId, value.getMessageId()),
                () -> assertEquals("deleteMessage", value.getMethod())
        );
    }


    @Test
    void sendAnswerCallbackQuery() throws TelegramApiException {
        String callbackQueryId = "12356";
        String text = "text message";
        boolean showAlert = true;
        responseSender.sendAnswerCallbackQuery(callbackQueryId, text, showAlert);
        ArgumentCaptor<AnswerCallbackQuery> argumentCaptor = ArgumentCaptor.forClass(AnswerCallbackQuery.class);
        verify(bot, times(1)).execute(argumentCaptor.capture());
        AnswerCallbackQuery value = argumentCaptor.getValue();
        assertAll(
                () -> assertEquals(callbackQueryId, value.getCallbackQueryId()),
                () -> assertEquals(text, value.getText()),
                () -> assertEquals(showAlert, value.getShowAlert()),
                () -> assertNull(value.getCacheTime()),
                () -> assertEquals("answercallbackquery", value.getMethod())
        );
    }
}