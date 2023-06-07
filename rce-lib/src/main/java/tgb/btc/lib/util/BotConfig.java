package tgb.btc.lib.util;


import lombok.extern.slf4j.Slf4j;
import tgb.btc.lib.enums.BotProperties;
import tgb.btc.lib.exception.InitPropertyValueNotFoundException;
import tgb.btc.lib.exception.PropertyValueNotFoundException;
import tgb.btc.lib.service.BeanHolder;

@Slf4j
public final class BotConfig {
    private BotConfig() {
    }

    public static void init() throws InitPropertyValueNotFoundException {
        CommandProcessorLoader.scan();
        BeanHolder.load();
        try {
            for (BotProperties botProperties : BotProperties.values()) {
                if (!botProperties.getIsBufferProperties()) {
                    botProperties.validate();
                    botProperties.load();
                }
            }
        } catch (PropertyValueNotFoundException e) {
            log.error(e.getMessage(),e);
            throw new InitPropertyValueNotFoundException(e.getMessage());
        }
    }
}
