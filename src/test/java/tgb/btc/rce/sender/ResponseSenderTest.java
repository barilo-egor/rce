package tgb.btc.rce.sender;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.rce.bot.RceBot;
import tgb.btc.rce.service.impl.keyboard.KeyboardBuildService;
import tgb.btc.rce.vo.InlineButton;

import java.io.File;
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
    protected void sendMessageText() throws TelegramApiException {
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
    protected void sendMessageTextReply() throws TelegramApiException {
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
    protected void sendMessageTextWithKeyboard() throws TelegramApiException {
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
    protected void sendMessageTextWithInlineButtons() throws TelegramApiException {
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
    protected void sendMessageTextWithListInlineButtons() throws TelegramApiException {
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
    protected void sendMessageReturnNullIfTelegramApiException() throws TelegramApiException {
        when(bot.execute((SendMessage) any())).thenThrow(TelegramApiException.class);
        assertEquals(Optional.empty(), responseSender.sendMessage(123456789L, "text"));
    }

    @Test
    protected void sendMessageThrowsSendMessage() throws TelegramApiException {
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
    protected void sendPhotoInputFileWithCaption() throws TelegramApiException {
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
    protected void sendPhotoByFileIdWithCaptionAndKeyboard() throws TelegramApiException {
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
    protected void sendPhotoByFileIdWithCaption() throws TelegramApiException {
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
    protected void sendPhotoInputFileWithCaptionAndKeyboard() throws TelegramApiException {
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
    protected void sendPhotoReturnNullIfTelegramApiException() throws TelegramApiException {
        when(bot.execute((SendPhoto) any())).thenThrow(TelegramApiException.class);
        assertEquals(Optional.empty(),
                responseSender.sendPhoto(123456789L, "caption", new InputFile("photo"), new InlineKeyboardMarkup()));
    }

    @Test
    protected void sendAnimation() throws TelegramApiException {
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
    protected void sendAnimationWithCaptionAndKeyboard() throws TelegramApiException {
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
    protected void sendAnimationInputFileWithCaptionAndKeyboard() throws TelegramApiException {
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
    protected void sendAnimationReturnNullIfTelegramApiException() throws TelegramApiException {
        when(bot.execute((SendAnimation) any())).thenThrow(TelegramApiException.class);
        assertNull(responseSender.sendAnimation(123456789L,
                new InputFile("animation"), "caption", new InlineKeyboardMarkup()));
    }

    @Test
    protected void sendEditedMessageText() throws TelegramApiException {
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
    protected void sendEditedMessageTextWithButtons() throws TelegramApiException {
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
    protected void sendEditedMessageTextWithReplyKeyboard() throws TelegramApiException {
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
    protected void sendEditedMessageTextWithInlineKeyboardMarkup() throws TelegramApiException {
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
}