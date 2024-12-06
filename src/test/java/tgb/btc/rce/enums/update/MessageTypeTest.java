package tgb.btc.rce.enums.update;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.games.Animation;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageTypeTest {
    @Test
    void shouldReturnText() {
        Message message = new Message();
        message.setText("/start");
        assertEquals(MessageType.TEXT, MessageType.fromMessage(message));
    }

    @Test
    void shouldReturnPhoto() {
        Message message = new Message();
        message.setPhoto(List.of(new PhotoSize()));
        assertEquals(MessageType.PHOTO, MessageType.fromMessage(message));
    }

    @Test
    void shouldReturnVideo() {
        Message message = new Message();
        message.setVideo(new Video());
        assertEquals(MessageType.VIDEO, MessageType.fromMessage(message));
    }

    @Test
    void shouldReturnAudio() {
        Message message = new Message();
        message.setAudio(new Audio());
        assertEquals(MessageType.AUDIO, MessageType.fromMessage(message));
    }

    @Test
    void shouldReturnDocument() {
        Message message = new Message();
        message.setDocument(new Document());
        assertEquals(MessageType.DOCUMENT, MessageType.fromMessage(message));
    }

    @Test
    void shouldReturnSticker() {
        Message message = new Message();
        message.setSticker(new Sticker());
        assertEquals(MessageType.STICKER, MessageType.fromMessage(message));
    }

    @Test
    void shouldReturnAnimation() {
        Message message = new Message();
        message.setAnimation(new Animation());
        assertEquals(MessageType.ANIMATION, MessageType.fromMessage(message));
    }

    @Test
    void shouldReturnVoice() {
        Message message = new Message();
        message.setVoice(new Voice());
        assertEquals(MessageType.VOICE, MessageType.fromMessage(message));
    }

    @Test
    void shouldReturnVideoNote() {
        Message message = new Message();
        message.setVideoNote(new VideoNote());
        assertEquals(MessageType.VIDEO_NOTE, MessageType.fromMessage(message));
    }

    @Test
    void shouldReturnContact() {
        Message message = new Message();
        message.setContact(new Contact());
        assertEquals(MessageType.CONTACT, MessageType.fromMessage(message));
    }

    @Test
    void shouldReturnLocation() {
        Message message = new Message();
        message.setLocation(new Location());
        assertEquals(MessageType.LOCATION, MessageType.fromMessage(message));
    }

    @Test
    void shouldReturnVenue() {
        Message message = new Message();
        message.setVenue(new Venue());
        assertEquals(MessageType.VENUE, MessageType.fromMessage(message));
    }

    @Test
    void shouldReturnPoll() {
        Message message = new Message();
        message.setPoll(new Poll());
        assertEquals(MessageType.POLL, MessageType.fromMessage(message));
    }

    @Test
    void shouldReturnDice() {
        Message message = new Message();
        message.setDice(new Dice());
        assertEquals(MessageType.DICE, MessageType.fromMessage(message));
    }

    @Test
    void shouldThrowExceptionForUnknownType() {
        Message message = new Message();
        assertThrows(IllegalArgumentException.class, () -> {
            MessageType.fromMessage(message);
        }, "Неизвестный тип сообщения");
    }
}