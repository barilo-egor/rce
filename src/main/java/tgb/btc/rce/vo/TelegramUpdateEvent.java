package tgb.btc.rce.vo;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Ивент с апдейтом
 */
@Getter
public class TelegramUpdateEvent extends ApplicationEvent {

    /**
     * Апдейт телеграма
     */
    private final Update update;

    public TelegramUpdateEvent(Object source, Update update) {
        super(source);
        this.update = update;
    }

}
