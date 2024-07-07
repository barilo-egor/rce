package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class BotConfig {
    private BotConfig() {
    }

    public static void init() {
        BeanHolder.load();
    }
}
