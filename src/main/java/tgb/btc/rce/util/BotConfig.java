package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.rce.exception.InitPropertyValueNotFoundException;
import tgb.btc.rce.exception.PropertyValueNotFoundException;

@Slf4j
public final class BotConfig {
    private BotConfig() {
    }

    public static void init() throws InitPropertyValueNotFoundException {
        CommandProcessorLoader.scan();
        try {
            BulkDiscountUtil.load(null);
        } catch (PropertyValueNotFoundException e) {
            log.error(e.getMessage(),e);
            throw new InitPropertyValueNotFoundException(e.getMessage());
        }
    }
}
