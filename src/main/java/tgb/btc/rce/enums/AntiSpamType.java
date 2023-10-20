package tgb.btc.rce.enums;

import tgb.btc.rce.enums.properties.BotProperties;
import tgb.btc.rce.service.Module;

public enum AntiSpamType implements Module {
    NONE,
    PICTURE,
    EMOJI;

    public static final AntiSpamType CURRENT =
            AntiSpamType.valueOf(BotProperties.MODULES.getString("anti.spam"));

    @Override
    public boolean isCurrent() {
        return this.equals(CURRENT);
    }
}
