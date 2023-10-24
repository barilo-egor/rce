package tgb.btc.rce.enums;

import tgb.btc.library.constants.enums.properties.CommonProperties;
import tgb.btc.library.interfaces.Module;

public enum ReferralType implements Module {
    NONE,
    STANDARD;

    public static final ReferralType CURRENT =
            ReferralType.valueOf(CommonProperties.MODULES.getString("referral.type"));

    @Override
    public boolean isCurrent() {
        return this.equals(CURRENT);
    }
}
