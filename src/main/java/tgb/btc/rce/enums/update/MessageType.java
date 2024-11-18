package tgb.btc.rce.enums.update;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;

/**
 * Перечисление типов сообщений в Telegram.
 */
public enum MessageType {
    /**
     * Текстовое сообщение.
     */
    TEXT,

    /**
     * Сообщение с фотографией.
     */
    PHOTO,

    /**
     * Сообщение с видео.
     */
    VIDEO,

    /**
     * Сообщение с аудиофайлом.
     */
    AUDIO,

    /**
     * Сообщение с документом (например, PDF, DOC).
     */
    DOCUMENT,

    /**
     * Сообщение со стикером.
     */
    STICKER,

    /**
     * Сообщение с анимацией (GIF или H.264/MPEG-4 Part 10).
     */
    ANIMATION,

    /**
     * Голосовое сообщение.
     */
    VOICE,

    /**
     * Сообщение с видео-заметкой (круглое видео).
     */
    VIDEO_NOTE,

    /**
     * Сообщение с контактной информацией.
     */
    CONTACT,

    /**
     * Сообщение с местоположением.
     */
    LOCATION,

    /**
     * Сообщение с местом (venue), которое включает адрес и название.
     */
    VENUE,

    /**
     * Сообщение с опросом (poll).
     */
    POLL,

    /**
     * Сообщение с кубиком, для интерактивного броска.
     */
    DICE;

    /**
     * Определяет тип сообщения на основе содержимого объекта Message.
     *
     * @param message объект сообщения, который нужно классифицировать
     * @return тип сообщения как MessageType
     */
    public static MessageType fromMessage(Message message) {
        if (message.hasText()) return TEXT;
        if (message.hasPhoto()) return PHOTO;
        if (message.hasVideo()) return VIDEO;
        if (message.hasAudio()) return AUDIO;
        if (message.hasDocument()) return DOCUMENT;
        if (message.hasSticker()) return STICKER;
        if (message.hasAnimation()) return ANIMATION;
        if (message.hasVoice()) return VOICE;
        if (message.hasVideoNote()) return VIDEO_NOTE;
        if (message.hasContact()) return CONTACT;
        if (message.hasLocation()) return LOCATION;
        if (Objects.nonNull(message.getVenue())) return VENUE;
        if (message.hasPoll()) return POLL;
        if (message.hasDice()) return DICE;

        throw new IllegalArgumentException("Неизвестный тип сообщения");
    }
}
