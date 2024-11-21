package tgb.btc.rce.service.handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.update.UpdateType;

/**
 * Обработчик типа апдейта
 */
public interface IUpdateHandler {

    /**
     * Обработка апдейта
     * @param update апдейт, который следует обработать
     */
    boolean handle(Update update);

    /**
     * Тип обрабатываемых апдейтов
     * @return тип апдейта
     */
    UpdateType getUpdateType();
}
