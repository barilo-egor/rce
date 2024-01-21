package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.rce.service.BeanHolder;

@Slf4j
public final class BotConfig {
    private BotConfig() {
    }

    public static void init() {
        CommandProcessorLoader.scan();
        BeanHolder.load();
    }
}
