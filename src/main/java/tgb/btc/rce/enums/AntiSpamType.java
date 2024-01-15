package tgb.btc.rce.enums;

import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.interfaces.Module;

public enum AntiSpamType implements Module {
    NONE,
    PICTURE,
    EMOJI;

    public static final AntiSpamType CURRENT =
            AntiSpamType.valueOf(PropertiesPath.MODULES_PROPERTIES.getString("anti.spam"));

    @Override
    public boolean isCurrent() {
        return this.equals(CURRENT);
    }
}
