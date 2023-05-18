package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.rce.exception.InitPropertyValueNotFoundException;
import tgb.btc.rce.exception.PropertyValueNotFoundException;
import tgb.btc.rce.service.BeanHolder;

@Slf4j
public final class BotConfig {
    private BotConfig() {
    }

    public static void init() throws InitPropertyValueNotFoundException {
        CommandProcessorLoader.scan();
        BeanHolder.load();
        try {
            BulkDiscountUtil.load(null);
        } catch (PropertyValueNotFoundException e) {
            log.error(e.getMessage(),e);
            throw new InitPropertyValueNotFoundException(e.getMessage());
        }
    }
}
