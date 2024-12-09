package tgb.btc.rce.sender;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.service.bean.bot.user.ReadUserService;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.MessageImage;
import tgb.btc.rce.service.impl.MessageImageService;
import tgb.btc.rce.service.impl.util.MenuService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageImageResponseSenderTest {

    @Mock
    private MessageImageService messageImageService;

    @Mock
    private ResponseSender responseSender;

    @Mock
    private MenuService menuService;

    @Mock
    private ReadUserService readUserService;

    @InjectMocks
    private MessageImageResponseSender messageImageResponseSender;

    @Test
    @DisplayName("Должен отправить изображение.")
    protected void sendMessageWithCustomTextShouldSendPhoto() {
        String fileId = "CgACAgIAAxkDAAIS42c7KceBwAUkHv6iuwABcJqHcm4nkgACXWUAAp882UlT-gJmcdZcGTYE";
        when(messageImageService.getFileId(any())).thenReturn(fileId);
        Long chatId = 123456789L;
        String message = "message";
        ReplyKeyboard replyKeyboard = new ReplyKeyboardMarkup();
        MessageImage messageImage = MessageImage.BOT_OFF;
        messageImageResponseSender.sendMessage(messageImage, chatId, message, replyKeyboard);
        verify(responseSender, times(1)).sendPhoto(chatId, message, fileId, replyKeyboard);
    }

    @Test
    @DisplayName("Должен отправить сообщение.")
    protected void sendMessageWithCustomTextShouldSendMessage() {
        when(messageImageService.getFileId(any())).thenReturn(null);
        Long chatId = 123456789L;
        String message = "message";
        ReplyKeyboard replyKeyboard = new ReplyKeyboardMarkup();
        MessageImage messageImage = MessageImage.BOT_OFF;
        messageImageResponseSender.sendMessage(messageImage, chatId, message, replyKeyboard);
        verify(responseSender, times(1)).sendMessage(chatId, message, replyKeyboard);
    }

    @Test
    @DisplayName("Должен отправить изображение.")
    protected void sendMessageShouldSendJpgPhoto() {
        String fileId = "CgACAgIAAxkDAAIS42c7KceBwAUkHv6iuwABcJqHcm4nkgACXWUAAp882UlT-gJmcdZcGTYE";
        when(messageImageService.getFileId(any())).thenReturn(fileId);
        Long chatId = 123456789L;
        String message = "message";
        ReplyKeyboard replyKeyboard = new ReplyKeyboardMarkup();
        MessageImage messageImage = MessageImage.BOT_OFF;
        when(messageImageService.getMessage(messageImage)).thenReturn(message);
        when(messageImageService.getFormat(messageImage)).thenReturn(".jpg");
        messageImageResponseSender.sendMessage(messageImage, chatId, replyKeyboard);
        verify(responseSender, times(1)).sendPhoto(chatId, message, fileId, replyKeyboard);
    }

    @Test
    @DisplayName("Должен отправить изображение.")
    protected void sendMessageShouldSendPngPhoto() {
        String fileId = "CgACAgIAAxkDAAIS42c7KceBwAUkHv6iuwABcJqHcm4nkgACXWUAAp882UlT-gJmcdZcGTYE";
        when(messageImageService.getFileId(any())).thenReturn(fileId);
        Long chatId = 123456789L;
        String message = "message";
        ReplyKeyboard replyKeyboard = new ReplyKeyboardMarkup();
        MessageImage messageImage = MessageImage.BOT_OFF;
        when(messageImageService.getMessage(messageImage)).thenReturn(message);
        when(messageImageService.getFormat(messageImage)).thenReturn(".png");
        messageImageResponseSender.sendMessage(messageImage, chatId, replyKeyboard);
        verify(responseSender, times(1)).sendPhoto(chatId, message, fileId, replyKeyboard);
    }

    @Test
    @DisplayName("Должен отправить изображение.")
    protected void sendMessageShouldSendJpegPhoto() {
        String fileId = "CgACAgIAAxkDAAIS42c7KceBwAUkHv6iuwABcJqHcm4nkgACXWUAAp882UlT-gJmcdZcGTYE";
        when(messageImageService.getFileId(any())).thenReturn(fileId);
        Long chatId = 123456789L;
        String message = "message";
        ReplyKeyboard replyKeyboard = new ReplyKeyboardMarkup();
        MessageImage messageImage = MessageImage.BOT_OFF;
        when(messageImageService.getMessage(messageImage)).thenReturn(message);
        when(messageImageService.getFormat(messageImage)).thenReturn(".jpeg");
        messageImageResponseSender.sendMessage(messageImage, chatId, replyKeyboard);
        verify(responseSender, times(1)).sendPhoto(chatId, message, fileId, replyKeyboard);
    }

    @Test
    @DisplayName("Должен отправить изображение.")
    protected void sendMessageShouldSendAnimation() {
        String fileId = "CgACAgIAAxkDAAIS42c7KceBwAUkHv6iuwABcJqHcm4nkgACXWUAAp882UlT-gJmcdZcGTYE";
        when(messageImageService.getFileId(any())).thenReturn(fileId);
        Long chatId = 123456789L;
        String message = "message";
        ReplyKeyboard replyKeyboard = new ReplyKeyboardMarkup();
        MessageImage messageImage = MessageImage.BOT_OFF;
        when(messageImageService.getMessage(messageImage)).thenReturn(message);
        when(messageImageService.getFormat(messageImage)).thenReturn(".mp4");
        messageImageResponseSender.sendMessage(messageImage, chatId, replyKeyboard);
        verify(responseSender, times(1)).sendAnimation(chatId, message, fileId, replyKeyboard);
    }

    @Test
    @DisplayName("Должен отправить изображение.")
    protected void sendMessageShouldThrowException() {
        String fileId = "CgACAgIAAxkDAAIS42c7KceBwAUkHv6iuwABcJqHcm4nkgACXWUAAp882UlT-gJmcdZcGTYE";
        when(messageImageService.getFileId(any())).thenReturn(fileId);
        Long chatId = 123456789L;
        String message = "message";
        ReplyKeyboard replyKeyboard = new ReplyKeyboardMarkup();
        MessageImage messageImage = MessageImage.BOT_OFF;
        when(messageImageService.getMessage(messageImage)).thenReturn(message);
        when(messageImageService.getFormat(messageImage)).thenReturn(".pdf");
        assertThrows(BaseException.class, () -> messageImageResponseSender.sendMessage(messageImage, chatId, replyKeyboard),
                "Для формата .pdf не предусмотрена реализация отправки изображения.");
    }

    @Test
    @DisplayName("Должен отправить изображение.")
    protected void sendMessageShouldSendMessage() {
        when(messageImageService.getFileId(any())).thenReturn(null);
        Long chatId = 123456789L;
        String message = "message";
        ReplyKeyboard replyKeyboard = new ReplyKeyboardMarkup();
        MessageImage messageImage = MessageImage.BOT_OFF;
        when(messageImageService.getMessage(messageImage)).thenReturn(message);
        messageImageResponseSender.sendMessage(messageImage, chatId, replyKeyboard);
        verify(responseSender, times(1)).sendMessage(chatId, message, replyKeyboard);
    }
    @Test
    @DisplayName("Должен отправить изображение.")
    protected void sendMessageShouldSendMessageWithoutReplyKeyboard() {
        when(messageImageService.getFileId(any())).thenReturn(null);
        Long chatId = 123456789L;
        String message = "message";
        MessageImage messageImage = MessageImage.BOT_OFF;
        when(messageImageService.getMessage(messageImage)).thenReturn(message);
        messageImageResponseSender.sendMessage(messageImage, chatId);
        verify(responseSender, times(1)).sendMessage(chatId, message);
    }

    @Test
    @DisplayName("Должен отправить изображение.")
    protected void sendMessageShouldSendMessageWithMenu() {
        when(messageImageService.getFileId(any())).thenReturn(null);
        Long chatId = 123456789L;
        String message = "message";
        ReplyKeyboard replyKeyboard = new ReplyKeyboardMarkup();
        MessageImage messageImage = MessageImage.BOT_OFF;
        when(messageImageService.getMessage(messageImage)).thenReturn(message);
        Menu menu = Menu.MAIN;
        UserRole userRole = UserRole.USER;
        when(readUserService.getUserRoleByChatId(chatId)).thenReturn(userRole);
        when(menuService.build(menu, userRole)).thenReturn(replyKeyboard);
        messageImageResponseSender.sendMessage(messageImage, menu, chatId);
        verify(responseSender, times(1)).sendMessage(chatId, message, replyKeyboard);
    }
}