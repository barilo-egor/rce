package tgb.btc.rce.util;

public final class BotConfig {
    private BotConfig() {
    }

    public static void init() {
        CommandProcessorLoader.scan();
        MessagePropertiesUtil.loadProperties();
    }
}
