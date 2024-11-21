package tgb.btc.rce.enums.update;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Set;

/**
 * Типы апдейтов телеграма
 */
public enum UpdateType {
    /**
     * Новое входящее сообщение, содержащее текст, медиа, локацию или другой контент.
     * Обычно используется для обработки текстовых команд и медиа.
     */
    MESSAGE,

    /**
     * Входящий встроенный запрос (inline query), когда пользователь вводит запрос с @имя_бота в чате.
     * Используется для предоставления результатов поиска.
     */
    INLINE_QUERY,

    /**
     * Встроенный результат запроса, выбранный пользователем и отправленный в чат.
     * Полезно для отслеживания популярных результатов встроенных запросов.
     */
    CHOSEN_INLINE_QUERY,

    /**
     * Обратный вызов (callback query), который срабатывает, когда пользователь нажимает на кнопку
     * встроенной клавиатуры с данными обратного вызова.
     * Обычно используется для обработки действий, связанных с кнопками.
     */
    CALLBACK_QUERY,

    /**
     * Отредактированное сообщение, отправленное пользователем.
     * Позволяет боту реагировать на изменения в сообщении.
     */
    EDITED_MESSAGE,

    /**
     * Новое сообщение, опубликованное в канале, где бот добавлен в качестве администратора.
     * Позволяет боту отслеживать или реагировать на публикации в канале.
     */
    CHANNEL_POST,

    /**
     * Отредактированное сообщение в канале.
     * Используется для обработки изменений сообщений в каналах.
     */
    EDITED_CHANNEL_POST,

    /**
     * Запрос на доставку при использовании Telegram Payments.
     * Используется для обработки доставки и расчета стоимости перед подтверждением оплаты.
     */
    SHIPPING_QUERY,

    /**
     * Запрос перед завершением оплаты в Telegram Payments.
     * Позволяет проверить данные перед завершением транзакции.
     */
    PRE_CHECKOUT_QUERY,

    /**
     * Обновление, содержащее информацию о новом опросе, который бот создал или который проходит в чате.
     * Позволяет управлять и обрабатывать опросы.
     */
    POLL,

    /**
     * Обновление с новым ответом на опрос от пользователя.
     * Полезно для отслеживания ответов на опрос и реагирования на их изменение.
     */
    POLL_ANSWER,

    /**
     * Обновление о статусе бота в чате (например, когда бот добавлен, удален или изменены его права).
     * Позволяет определять доступные возможности бота в чате.
     */
    MY_CHAT_MEMBER,

    /**
     * Обновление о статусе участника чата, не являющегося ботом (например, когда пользователь добавлен в группу, удален или изменены его права).
     * Полезно для отслеживания активности пользователей и управления правами доступа.
     */
    CHAT_MEMBER,

    /**
     * Запрос на вступление пользователя в чат (актуально для публичных групп с настройкой "Запросить одобрение на вступление").
     * Позволяет боту автоматически принимать или отклонять запросы на вступление.
     */
    CHAT_JOIN_REQUEST;

    public static final Set<UpdateType> STATE_UPDATE_TYPES = Set.of(MESSAGE, CALLBACK_QUERY, INLINE_QUERY);

    /**
     * Получение типа апдейта
     * @param update апдейт
     * @return тип апдейта
     */
    public static UpdateType fromUpdate(Update update) {
        if (update.hasMessage()) return MESSAGE;
        if (update.hasInlineQuery()) return INLINE_QUERY;
        if (update.hasCallbackQuery()) return CALLBACK_QUERY;
        if (update.hasEditedMessage()) return EDITED_MESSAGE;
        if (update.hasChannelPost()) return CHANNEL_POST;
        if (update.hasEditedChannelPost()) return EDITED_CHANNEL_POST;
        if (update.hasShippingQuery()) return SHIPPING_QUERY;
        if (update.hasPreCheckoutQuery()) return PRE_CHECKOUT_QUERY;
        if (update.hasPoll()) return POLL;
        if (update.hasPollAnswer()) return POLL_ANSWER;
        if (update.hasMyChatMember()) return MY_CHAT_MEMBER;
        if (update.hasChatMember()) return CHAT_MEMBER;
        if (update.hasChatJoinRequest()) return CHAT_JOIN_REQUEST;
        throw new RuntimeException("Неизвестный тип у update.");
    }

    /**
     * Получение chat id
     * @param update апдейт
     * @return chatId
     */
    public static Long getChatId(Update update) {
        if (update.hasMessage()) return update.getMessage().getChatId();
        if (update.hasInlineQuery()) return update.getInlineQuery().getFrom().getId();
        if (update.hasCallbackQuery()) return update.getCallbackQuery().getFrom().getId();
        if (update.hasEditedMessage()) return update.getEditedMessage().getChatId();
        if (update.hasChannelPost()) return update.getChannelPost().getChatId();
        if (update.hasEditedChannelPost()) return update.getEditedChannelPost().getChatId();
        if (update.hasShippingQuery()) return update.getShippingQuery().getFrom().getId();
        if (update.hasPreCheckoutQuery()) return update.getPreCheckoutQuery().getFrom().getId();
        if (update.hasPoll() || update.hasPollAnswer()) {
            return null;
        }
        if (update.hasMyChatMember()) return update.getMyChatMember().getChat().getId();
        if (update.hasChatMember()) return update.getChatMember().getChat().getId();
        if (update.hasChatJoinRequest()) return update.getChatJoinRequest().getChat().getId();
        return null;
    }
}
